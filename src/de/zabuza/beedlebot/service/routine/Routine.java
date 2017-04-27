package de.zabuza.beedlebot.service.routine;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.databridge.EPhase;
import de.zabuza.beedlebot.databridge.io.PushDataService;
import de.zabuza.beedlebot.exceptions.ItemCategoryNotOpenedException;
import de.zabuza.beedlebot.exceptions.NotAtCentralTradersDepotException;
import de.zabuza.beedlebot.exceptions.PageContentWrongFormatException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.logging.LoggerUtil;
import de.zabuza.beedlebot.service.Service;
import de.zabuza.beedlebot.service.routine.tasks.AnalyzeTask;
import de.zabuza.beedlebot.service.routine.tasks.PurchaseTask;
import de.zabuza.beedlebot.service.routine.tasks.WaitForDeliveryTask;
import de.zabuza.beedlebot.store.EItemCategory;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.beedlebot.store.Store;
import de.zabuza.sparkle.freewar.IFreewarInstance;

/**
 * The actual routine of the service which analyzes, purchases and sells items
 * from the central traders depot. The routine works in rounds, once created use
 * {@link #update()} to spend the routine a round of processing. In a round it
 * will only execute small steps and quickly return so that it does not slow
 * down parent processes. You may just bind it into a life cycle of a
 * controlling thread. With {@link #getPhase()} the current phase of the routine
 * can be accessed. The method {@link #reset()} can be used to reset the routine
 * to its initial situation and begin with the first phase again. Fetch the
 * progress of the routine with methods {@link #fetchBoughtItems()},
 * {@link #getTotalCost()} and {@link #getTotalProfit()}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class Routine {
	/**
	 * Time to pass until the method will check if a delivery arrived instead of
	 * checking it every phase of {@link #update()}. This is done to not
	 * over-stress the driver resource and minimize
	 * {@link StaleElementReferenceException}s.
	 */
	private final static long AWAITING_DELIVERY_YIELD_UNTIL = 4_000;
	/**
	 * Amount of how many update phases the routine is allowed to use for
	 * resolving a problem by itself. If it does not resolve the problem within
	 * this limit it must give up and throw the problem to parent objects.
	 */
	private final static int PROBLEM_SELF_RESOLVING_TRIES_MAX = 5;
	/**
	 * Amount of how many update phases the routine needs to pass without
	 * encountering a problem until it is allowed to reset the resolving tries
	 * counter.
	 */
	private static final int PROBLEM_SELF_RESOLVING_TRIES_RESET = 3;
	/**
	 * Time to pass until the method will check if the player is able to move
	 * instead of checking it every phase of {@link #update()}. This is done to
	 * not over-stress the driver resource and minimize
	 * {@link StaleElementReferenceException}s.
	 */
	private final static long WAIT_FOR_CAN_MOVE_YIELD_UNTIL = 2_000;
	/**
	 * Buffer that contains all bought items. Can be used by a parent object to
	 * fetch the progress of the routine.
	 */
	private Queue<Item> mBoughtItemsBuffer;
	/**
	 * The location in Freewar of the central traders depot. The routine can
	 * only proceed when at this location.
	 */
	private final Point mCentralTradersDepot;
	/**
	 * The result of the last item analyze.
	 */
	private AnalyzeResult mCurrentAnalyzeResult;
	/**
	 * The current item category to process in an analyze.
	 */
	private EItemCategory mCurrentCategory;
	/**
	 * The driver to use for accessing browser contents.
	 */
	private final WebDriver mDriver;
	/**
	 * The Freewar instance to use for accessing the games contents.
	 */
	private final IFreewarInstance mInstance;
	/**
	 * The timestamp of when the last check of if an item delivery arrived was.
	 */
	private long mLastAwaitingDeliveryTimestamp;
	/**
	 * The timestamp of when the last check of if the player is able to move
	 * was.
	 */
	private long mLastWaitForCanMoveTimestamp;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;
	/**
	 * The navigator to use for navigation menus at the central traders depot.
	 */
	private final CentralTradersDepotNavigator mNavigator;
	/**
	 * The current phase the routine is in.
	 */
	private EPhase mPhase;
	/**
	 * Amount of how often after encountering a problem the routine has
	 * successfully passes an update phase without encountering a problem again.
	 */
	private int mProblemSelfResolvingPhasesWithoutProblem;
	/**
	 * Amount of how often the routine has tried to resolve a problem by itself
	 * in a row. The counter is reseted once it finishes an update phase without
	 * problems and increased whenever an error occurs that the routine likes to
	 * resolve by itself.
	 */
	private int mProblemSelfResolvingTries;
	/**
	 * The service to use for pushing data over the {@link DataBridge}.
	 */
	private final PushDataService mPushDataService;
	/**
	 * The service to use for callback when encountering a problem that needs to
	 * be resolved.
	 */
	private final Service mService;
	/**
	 * The store to use for accessing item price data.
	 */
	private final Store mStore;
	/**
	 * The total cost of all items bought with this routine instance.
	 */
	private int mTotalCost;
	/**
	 * The total profit made with all bought items of this routine instance.
	 */
	private int mTotalProfit;
	/**
	 * The task to use for waiting to the next item delivery.
	 */
	private final WaitForDeliveryTask mWaitForDeliveryTask;
	/**
	 * Whether there was a problem in the last update phase of the routine or
	 * not.
	 */
	private boolean mWasProblemLastUpdate;

	/**
	 * Creates a new instance of a routine with the given data. The routine
	 * works in rounds, once created use {@link #update()} to spend the routine
	 * a round of processing. In a round it will only execute small steps and
	 * quickly return so that it does not slow down parent processes. You may
	 * just bind it into a life cycle of a controlling thread. With
	 * {@link #getPhase()} the current phase of the routine can be accessed. The
	 * method {@link #reset()} can be used to reset the routine to its initial
	 * situation and begin with the first phase again. Fetch the progress of the
	 * routine with methods {@link #fetchBoughtItems()}, {@link #getTotalCost()}
	 * and {@link #getTotalProfit()}.
	 * 
	 * @param service
	 *            The service to use for callback when encountering problems
	 *            that need to be resolved
	 * @param instance
	 *            The instance to use for accessing the games contents
	 * @param driver
	 *            The driver to use for accessing browsers contents
	 * @param pushDataService
	 *            The service to use for pushing data to the {@link DataBridge}.
	 * @param store
	 *            The store to use for accessing item price data
	 */
	public Routine(final Service service, final IFreewarInstance instance, final WebDriver driver,
			final PushDataService pushDataService, final Store store) {
		this.mLogger = LoggerFactory.getLogger();
		this.mService = service;
		this.mInstance = instance;
		this.mDriver = driver;
		this.mPushDataService = pushDataService;
		this.mStore = store;
		this.mPhase = EPhase.ANALYZE;
		this.mCurrentCategory = null;
		this.mCurrentAnalyzeResult = null;
		this.mBoughtItemsBuffer = new LinkedList<>();
		this.mTotalProfit = 0;
		this.mTotalCost = 0;
		this.mWaitForDeliveryTask = new WaitForDeliveryTask(this.mInstance.getChat());
		this.mCentralTradersDepot = new Point(88, 89);
		this.mNavigator = new CentralTradersDepotNavigator(this.mInstance, this.mDriver);

		this.mLastAwaitingDeliveryTimestamp = 0;
		this.mLastWaitForCanMoveTimestamp = 0;

		this.mProblemSelfResolvingTries = 0;
		this.mProblemSelfResolvingPhasesWithoutProblem = 0;
		this.mWasProblemLastUpdate = false;
	}

	/**
	 * Fetch all items that the routine has bought since the last call of this
	 * method, like flushing a buffer. Can be used by a parent object to fetch
	 * the progress of the routine.
	 * 
	 * @return All items that the routine has bought since the last call of this
	 *         method, sorted by purchase timestamp, early dates first.
	 */
	public Queue<Item> fetchBoughtItems() {
		final Queue<Item> boughtItems = this.mBoughtItemsBuffer;
		this.mBoughtItemsBuffer = new LinkedList<>();
		return boughtItems;
	}

	/**
	 * Gets the current phase of the routine. Can be used by a parent object to
	 * fetch the progress of the routine.
	 * 
	 * @return The current phase of the routine
	 */
	public EPhase getPhase() {
		return this.mPhase;
	}

	/**
	 * Gets the total cost of all items bought with this routine instance.
	 * 
	 * @return The total cost of all items bought with this routine instance
	 */
	public int getTotalCost() {
		return this.mTotalCost;
	}

	/**
	 * Gets the total profit made with all bought items of this routine
	 * instance.
	 * 
	 * @return The total profit made with all bought items of this routine
	 *         instance
	 */
	public int getTotalProfit() {
		return this.mTotalProfit;
	}

	/**
	 * Resets the routine to its initial situation such that the next call of
	 * {@link #update()} will begin with the first phase again.
	 */
	public void reset() {
		setPhase(EPhase.ANALYZE);
		this.mCurrentCategory = null;
		this.mCurrentAnalyzeResult = null;

		this.mWasProblemLastUpdate = false;
		this.mProblemSelfResolvingTries = 0;
		this.mProblemSelfResolvingPhasesWithoutProblem = 0;
	}

	/**
	 * The routine works in rounds, once created this method is used to spend
	 * the routine a round of processing. In a round it will only execute small
	 * steps and quickly return so that it does not slow down parent processes.
	 * You may just bind it into a life cycle of a controlling thread. If a
	 * problem occurs that can not be resolved the method will catch it and
	 * callback with {@link Service#setProblem(Exception)} to the parent
	 * service. If the method changes the phase of the routine it will
	 * automatically update important data in the shared memory with
	 * {@link PushDataService#updateActiveData()}.
	 */
	public void update() {
		// Check the problem state
		if (!this.mWasProblemLastUpdate) {
			// There was no problem in the last round
			if (this.mProblemSelfResolvingTries > 0) {
				this.mProblemSelfResolvingPhasesWithoutProblem++;
				if (this.mProblemSelfResolvingPhasesWithoutProblem >= PROBLEM_SELF_RESOLVING_TRIES_RESET) {
					// Managed enough rounds without problem to reset the
					// counter
					this.mProblemSelfResolvingPhasesWithoutProblem = 0;
					this.mProblemSelfResolvingTries = 0;
				}
			}
		} else {
			// Reset the problem state for this round
			this.mWasProblemLastUpdate = false;
		}

		try {
			// Check if the current location is the central traders depot
			final Point currentLocation = this.mInstance.getLocation().getPosition();
			if (!currentLocation.equals(this.mCentralTradersDepot)) {
				throw new NotAtCentralTradersDepotException(currentLocation);
			}

			// Analyze phase
			if (getPhase() == EPhase.ANALYZE) {
				boolean finishedFullAnalyze = false;

				// Determine the category for this round
				if (this.mCurrentCategory == null || this.mCurrentCategory == EItemCategory.AMULET) {
					// First analyze round
					this.mCurrentCategory = EItemCategory.SPELL;
					this.mCurrentAnalyzeResult = new AnalyzeResult();
					this.mLogger.logInfo("Starting analyze");
				} else if (this.mCurrentCategory == EItemCategory.SPELL) {
					this.mCurrentCategory = EItemCategory.MISCELLANEOUS;
				} else if (this.mCurrentCategory == EItemCategory.MISCELLANEOUS) {
					this.mCurrentCategory = EItemCategory.ATTACK_WEAPON;
				} else if (this.mCurrentCategory == EItemCategory.ATTACK_WEAPON) {
					this.mCurrentCategory = EItemCategory.DEFENSE_WEAPON;
				} else {
					// Last analyze round
					this.mCurrentCategory = EItemCategory.AMULET;
					finishedFullAnalyze = true;
				}

				if (this.mLogger.isDebugEnabled()) {
					this.mLogger.logDebug("Starting analyze, selected category: " + this.mCurrentCategory);
				}

				// Start analyze task
				final AnalyzeTask analyzeTask = new AnalyzeTask(this.mDriver, this.mInstance.getFrameManager(),
						this.mCurrentAnalyzeResult, this.mCurrentCategory, this.mStore, this.mNavigator);
				analyzeTask.start();

				// Proceed to the next phase
				if (finishedFullAnalyze) {
					if (this.mCurrentAnalyzeResult.isEmpty()) {
						// There are no items, wait for next delivery
						setPhase(EPhase.AWAITING_DELIVERY);
					} else {
						// There are items, start buying but first ensure that
						// player is able to buy items
						setPhase(EPhase.WAIT);
					}
				}
				return;
			}

			// Purchase phase
			if (getPhase() == EPhase.PURCHASE) {
				final Item item = this.mCurrentAnalyzeResult.poll();

				// Abort and start waiting for the next delivery if there is no
				// item
				if (item == null) {
					setPhase(EPhase.AWAITING_DELIVERY);
					return;
				}

				// Start the purchase task
				final PurchaseTask purchaseTask = new PurchaseTask(this.mNavigator, this.mInstance.getPlayer(), item);
				purchaseTask.start();

				if (purchaseTask.wasBought()) {
					this.mStore.registerItemPurchase(item);
					this.mBoughtItemsBuffer.add(item);
					this.mTotalCost += item.getCost();
					this.mTotalProfit += item.getProfit();

					this.mLogger.logInfo("Bought item: " + item);
				} else {
					// Log the problem but continue
					this.mLogger.logInfo("Item not bought: " + item);
				}

				// Proceed to the next phase
				setPhase(EPhase.WAIT);
				return;
			}

			// Wait phase
			if (getPhase() == EPhase.WAIT) {
				final long currentTimestamp = System.currentTimeMillis();
				if (currentTimestamp - this.mLastWaitForCanMoveTimestamp < WAIT_FOR_CAN_MOVE_YIELD_UNTIL) {
					// Yield this iteration to not over-stress the movement
					// method
					return;
				}

				this.mLastWaitForCanMoveTimestamp = currentTimestamp;
				if (this.mInstance.getMovement().canMove()) {
					this.mLogger.logInfo("Waited for can move");

					// Proceed to the next phase
					setPhase(EPhase.PURCHASE);
				}

				return;
			}

			// Awaiting delivery phase
			if (getPhase() == EPhase.AWAITING_DELIVERY) {
				final long currentTimestamp = System.currentTimeMillis();
				if (currentTimestamp - this.mLastAwaitingDeliveryTimestamp < AWAITING_DELIVERY_YIELD_UNTIL) {
					// Yield this iteration to not over-stress the chat method
					return;
				}

				this.mLastAwaitingDeliveryTimestamp = currentTimestamp;
				this.mWaitForDeliveryTask.start();
				if (this.mWaitForDeliveryTask.wasThereADelivery()) {
					this.mLogger.logInfo("Waited for item delivery");

					// Proceed to the next phase
					setPhase(EPhase.ANALYZE);
				}
				return;
			}
		} catch (final StaleElementReferenceException | NoSuchElementException | TimeoutException
				| PageContentWrongFormatException | ItemCategoryNotOpenedException e) {
			if (this.mProblemSelfResolvingTries >= PROBLEM_SELF_RESOLVING_TRIES_MAX) {
				// The problem could not get resolved in the limit
				this.mWasProblemLastUpdate = true;
				this.mService.setProblem(e);
			} else {
				// Log the problem but continue
				this.mWasProblemLastUpdate = true;
				this.mProblemSelfResolvingTries++;
				this.mLogger.logError("Error while routine: " + LoggerUtil.getStackTrace(e));
			}
		} catch (final Exception e) {
			this.mWasProblemLastUpdate = true;
			this.mService.setProblem(e);
		}
	}

	/**
	 * Sets the current phase the routine is in. If the phase differs from the
	 * current phase it will trigger an update of important active data with
	 * {@link PushDataService#updateActiveData()}.
	 * 
	 * @param phase
	 *            The phase to set
	 */
	private void setPhase(final EPhase phase) {
		final EPhase oldPhase = this.mPhase;
		this.mPhase = phase;

		if (this.mPhase != oldPhase) {
			this.mPushDataService.updateActiveData();
		}
	}
}
