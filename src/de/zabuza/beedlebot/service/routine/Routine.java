package de.zabuza.beedlebot.service.routine;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.databridge.EPhase;
import de.zabuza.beedlebot.databridge.io.PushDataService;
import de.zabuza.beedlebot.exceptions.NotAtCentralTradersDepotException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.logging.LoggerUtil;
import de.zabuza.beedlebot.service.Service;
import de.zabuza.beedlebot.service.routine.tasks.AnalyseTask;
import de.zabuza.beedlebot.service.routine.tasks.PurchaseTask;
import de.zabuza.beedlebot.service.routine.tasks.WaitForDeliveryTask;
import de.zabuza.beedlebot.store.EItemCategory;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.beedlebot.store.Store;
import de.zabuza.sparkle.freewar.IFreewarInstance;

public final class Routine {

	private final static long AWAITING_DELIVERY_YIELD_UNTIL = 2000;
	private final static long WAIT_FOR_CAN_MOVE_YIELD_UNTIL = 2000;
	private Queue<Item> mBoughtItemsBuffer;
	private final Point mCentralTradersDepot;
	private AnalyseResult mCurrentAnalyseResult;
	private EItemCategory mCurrentCategory;
	private final WebDriver mDriver;
	private final IFreewarInstance mInstance;
	private long mLastAwaitingDeliveryTimestamp;
	private long mLastWaitForCanMoveTimestamp;
	private ILogger mLogger;
	private final CentralTradersDepotNavigator mNavigator;
	private EPhase mPhase;
	private final PushDataService mPushDataService;
	private final Service mService;
	private final Store mStore;
	private int mTotalCost;
	private int mTotalProfit;
	private WaitForDeliveryTask mWaitForDeliveryTask;

	public Routine(final Service service, final IFreewarInstance instance, final WebDriver driver,
			final PushDataService pushDataService, final Store store) {
		this.mLogger = LoggerFactory.getLogger();
		this.mService = service;
		this.mInstance = instance;
		this.mDriver = driver;
		this.mPushDataService = pushDataService;
		this.mStore = store;
		this.mPhase = EPhase.ANALYSE;
		this.mCurrentCategory = null;
		this.mCurrentAnalyseResult = null;
		this.mBoughtItemsBuffer = new LinkedList<>();
		this.mTotalProfit = 0;
		this.mTotalCost = 0;
		this.mWaitForDeliveryTask = new WaitForDeliveryTask(this.mInstance.getChat());
		this.mCentralTradersDepot = new Point(88, 89);
		this.mNavigator = new CentralTradersDepotNavigator(this.mInstance, this.mDriver);

		this.mLastAwaitingDeliveryTimestamp = 0;
		this.mLastWaitForCanMoveTimestamp = 0;
	}

	public Queue<Item> fetchBoughtItems() {
		Queue<Item> boughtItems = this.mBoughtItemsBuffer;
		this.mBoughtItemsBuffer = new LinkedList<>();
		return boughtItems;
	}

	public EPhase getPhase() {
		return this.mPhase;
	}

	public int getTotalCost() {
		return this.mTotalCost;
	}

	public int getTotalProfit() {
		return this.mTotalProfit;
	}

	public void reset() {
		setPhase(EPhase.ANALYSE);
		this.mCurrentCategory = null;
		this.mCurrentAnalyseResult = null;
	}

	public void update() {
		try {
			// Check if the current location is the central traders depot
			final Point currentLocation = this.mInstance.getLocation().getPosition();
			if (!currentLocation.equals(this.mCentralTradersDepot)) {
				throw new NotAtCentralTradersDepotException(currentLocation);
			}

			// Analyse phase
			if (getPhase() == EPhase.ANALYSE) {
				boolean finishedFullAnalyse = false;

				// Determine the category for this round
				if (this.mCurrentCategory == null || this.mCurrentCategory == EItemCategory.AMULET) {
					// First analyse round
					this.mCurrentCategory = EItemCategory.SPELL;
					this.mCurrentAnalyseResult = new AnalyseResult();
					this.mLogger.logInfo("Starting analyse");
				} else if (this.mCurrentCategory == EItemCategory.SPELL) {
					this.mCurrentCategory = EItemCategory.MISCELLANEOUS;
				} else if (this.mCurrentCategory == EItemCategory.MISCELLANEOUS) {
					this.mCurrentCategory = EItemCategory.ATTACK_WEAPON;
				} else if (this.mCurrentCategory == EItemCategory.ATTACK_WEAPON) {
					this.mCurrentCategory = EItemCategory.DEFENSE_WEAPON;
				} else {
					// Last analyse round
					this.mCurrentCategory = EItemCategory.AMULET;
					finishedFullAnalyse = true;
				}

				if (this.mLogger.isDebugEnabled()) {
					this.mLogger.logDebug("Starting analyse, selected category: " + this.mCurrentCategory);
				}

				// Start analyse task
				final AnalyseTask analyseTask = new AnalyseTask(this.mDriver, this.mInstance.getFrameManager(),
						this.mCurrentAnalyseResult, this.mCurrentCategory, this.mStore, this.mNavigator);
				analyseTask.start();

				// Proceed to the next phase
				if (finishedFullAnalyse) {
					if (this.mCurrentAnalyseResult.isEmpty()) {
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
				final Item item = this.mCurrentAnalyseResult.poll();

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
					setPhase(EPhase.ANALYSE);
				}
				return;
			}
		} catch (final StaleElementReferenceException e) {
			// Log the problem but continue
			this.mLogger.logError("Error while routine: " + LoggerUtil.getStackTrace(e));
		} catch (final Exception e) {
			this.mService.setProblem(e);
		}
	}

	private void setPhase(final EPhase phase) {
		final EPhase oldPhase = this.mPhase;
		this.mPhase = phase;

		if (this.mPhase != oldPhase) {
			this.mPushDataService.updateActiveData();
		}
	}
}
