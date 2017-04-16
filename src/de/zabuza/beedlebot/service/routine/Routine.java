package de.zabuza.beedlebot.service.routine;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.databridge.EPhase;
import de.zabuza.beedlebot.databridge.io.PushDataService;
import de.zabuza.beedlebot.exceptions.NotAtCentralTradersDepotException;
import de.zabuza.beedlebot.service.Service;
import de.zabuza.beedlebot.service.routine.tasks.AnalyseTask;
import de.zabuza.beedlebot.service.routine.tasks.PurchaseTask;
import de.zabuza.beedlebot.service.routine.tasks.WaitForDeliveryTask;
import de.zabuza.beedlebot.store.EItemCategory;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.beedlebot.store.Store;
import de.zabuza.sparkle.freewar.IFreewarInstance;

public final class Routine {

	private Queue<Item> mBoughtItemsBuffer;
	private final Point mCentralTradersDepot;
	private AnalyseResult mCurrentAnalyseResult;
	private EItemCategory mCurrentCategory;
	private final WebDriver mDriver;
	private final IFreewarInstance mInstance;
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

				// TODO Remove debug
				System.out.println("Analyse: Selected " + mCurrentCategory);

				// Start analyse task
				final AnalyseTask analyseTask = new AnalyseTask(mDriver, mCurrentAnalyseResult, mCurrentCategory,
						mStore, mNavigator);
				analyseTask.start();

				// Proceed to the next phase
				if (finishedFullAnalyse) {
					if (mCurrentAnalyseResult.isEmpty()) {
						// There are no items, wait for next delivery
						setPhase(EPhase.AWAITING_DELIVERY);
					} else {
						// There are items, start buying
						setPhase(EPhase.PURCHASE);
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
				final PurchaseTask purchaseTask = new PurchaseTask(mNavigator, item);
				purchaseTask.start();

				if (purchaseTask.wasBought()) {
					mStore.registerItemPurchase(item);
					mBoughtItemsBuffer.add(item);
					mTotalCost += item.getCost();
					mTotalProfit += item.getProfit();
					// TODO Remove debug print
					System.out.println("Bought: " + item.getName() + ", Profit: " + item.getProfit());
				} else {
					// TODO Error logging
					// Log the problem but continue
					System.out.println("Not bought, problem: " + item.getName());
				}

				// Proceed to the next phase
				setPhase(EPhase.WAIT);
				return;
			}

			// Wait phase
			if (getPhase() == EPhase.WAIT) {
				if (mInstance.getMovement().canMove()) {
					// TODO Remove debug
					System.out.println("Waited");

					// Proceed to the next phase
					setPhase(EPhase.PURCHASE);
				}

				return;
			}

			// Awaiting delivery phase
			if (getPhase() == EPhase.AWAITING_DELIVERY) {
				mWaitForDeliveryTask.start();
				if (mWaitForDeliveryTask.wasThereADelivery()) {
					// TODO Remove debug print
					System.out.println("Waited for delivery.");

					// Proceed to the next phase
					setPhase(EPhase.ANALYSE);
				}
				return;
			}
		} catch (final StaleElementReferenceException e) {
			// TODO Error logging
			// Log the problem but continue
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
