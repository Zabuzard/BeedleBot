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
		mLogger = LoggerFactory.getLogger();
		mService = service;
		mInstance = instance;
		mDriver = driver;
		mPushDataService = pushDataService;
		mStore = store;
		mPhase = EPhase.ANALYSE;
		mCurrentCategory = null;
		mCurrentAnalyseResult = null;
		mBoughtItemsBuffer = new LinkedList<>();
		mTotalProfit = 0;
		mTotalCost = 0;
		mWaitForDeliveryTask = new WaitForDeliveryTask(mInstance.getChat());
		mCentralTradersDepot = new Point(88, 89);
		mNavigator = new CentralTradersDepotNavigator(mInstance, mDriver);
		mLastAwaitingDeliveryTimestamp = System.currentTimeMillis();
		mLastWaitForCanMoveTimestamp = System.currentTimeMillis();
	}

	public Queue<Item> fetchBoughtItems() {
		Queue<Item> boughtItems = mBoughtItemsBuffer;
		mBoughtItemsBuffer = new LinkedList<>();
		return boughtItems;
	}

	public EPhase getPhase() {
		return mPhase;
	}

	public int getTotalCost() {
		return mTotalCost;
	}

	public int getTotalProfit() {
		return mTotalProfit;
	}

	public void reset() {
		setPhase(EPhase.ANALYSE);
		mCurrentCategory = null;
		mCurrentAnalyseResult = null;
	}

	public void update() {
		try {
			// Check if the current location is the central traders depot
			final Point currentLocation = mInstance.getLocation().getPosition();
			if (!currentLocation.equals(mCentralTradersDepot)) {
				throw new NotAtCentralTradersDepotException(currentLocation);
			}

			// Analyse phase
			if (getPhase() == EPhase.ANALYSE) {
				boolean finishedFullAnalyse = false;

				// Determine the category for this round
				if (mCurrentCategory == null || mCurrentCategory == EItemCategory.AMULET) {
					// First analyse round
					mCurrentCategory = EItemCategory.SPELL;
					mCurrentAnalyseResult = new AnalyseResult();
					mLogger.logInfo("Starting analyse");
				} else if (mCurrentCategory == EItemCategory.SPELL) {
					mCurrentCategory = EItemCategory.MISCELLANEOUS;
				} else if (mCurrentCategory == EItemCategory.MISCELLANEOUS) {
					mCurrentCategory = EItemCategory.ATTACK_WEAPON;
				} else if (mCurrentCategory == EItemCategory.ATTACK_WEAPON) {
					mCurrentCategory = EItemCategory.DEFENSE_WEAPON;
				} else {
					// Last analyse round
					mCurrentCategory = EItemCategory.AMULET;
					finishedFullAnalyse = true;
				}

				if (mLogger.isDebugEnabled()) {
					mLogger.logDebug("Starting analyse, selected category: " + mCurrentCategory);
				}

				// Start analyse task
				final AnalyseTask analyseTask = new AnalyseTask(mDriver, mInstance.getFrameManager(),
						mCurrentAnalyseResult, mCurrentCategory, mStore, mNavigator);
				analyseTask.start();

				// Proceed to the next phase
				if (finishedFullAnalyse) {
					if (mCurrentAnalyseResult.isEmpty()) {
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
				final Item item = mCurrentAnalyseResult.poll();

				// Abort and start waiting for the next delivery if there is no
				// item
				if (item == null) {
					setPhase(EPhase.AWAITING_DELIVERY);
					return;
				}

				// Start the purchase task
				final PurchaseTask purchaseTask = new PurchaseTask(mNavigator, mInstance.getPlayer(), item);
				purchaseTask.start();

				if (purchaseTask.wasBought()) {
					mStore.registerItemPurchase(item);
					mBoughtItemsBuffer.add(item);
					mTotalCost += item.getCost();
					mTotalProfit += item.getProfit();

					mLogger.logInfo("Bought item: " + item);
				} else {
					// Log the problem but continue
					mLogger.logInfo("Item not bought: " + item);
				}

				// Proceed to the next phase
				setPhase(EPhase.WAIT);
				return;
			}

			// Wait phase
			if (getPhase() == EPhase.WAIT) {
				final long currentTimestamp = System.currentTimeMillis();
				if (currentTimestamp - mLastWaitForCanMoveTimestamp < WAIT_FOR_CAN_MOVE_YIELD_UNTIL) {
					// Yield this iteration to not over-stress the movement
					// method
					return;
				}

				mLastWaitForCanMoveTimestamp = currentTimestamp;
				if (mInstance.getMovement().canMove()) {
					mLogger.logInfo("Waited for can move");

					// Proceed to the next phase
					setPhase(EPhase.PURCHASE);
				}

				return;
			}

			// Awaiting delivery phase
			if (getPhase() == EPhase.AWAITING_DELIVERY) {
				final long currentTimestamp = System.currentTimeMillis();
				if (currentTimestamp - mLastAwaitingDeliveryTimestamp < AWAITING_DELIVERY_YIELD_UNTIL) {
					// Yield this iteration to not over-stress the chat method
					return;
				}

				mLastAwaitingDeliveryTimestamp = currentTimestamp;
				mWaitForDeliveryTask.start();
				if (mWaitForDeliveryTask.wasThereADelivery()) {
					mLogger.logInfo("Waited for item delivery");

					// Proceed to the next phase
					setPhase(EPhase.ANALYSE);
				}
				return;
			}
		} catch (final StaleElementReferenceException e) {
			// Log the problem but continue
			mLogger.logError("Error while routine: " + LoggerUtil.getStackTrace(e));
		} catch (final Exception e) {
			mService.setProblem(e);
		}
	}

	private void setPhase(final EPhase phase) {
		final EPhase oldPhase = mPhase;
		mPhase = phase;

		if (mPhase != oldPhase) {
			mPushDataService.updateActiveData();
		}
	}
}
