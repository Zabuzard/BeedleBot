package de.zabuza.beedlebot.databridge.io;

import java.util.Queue;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.databridge.EPhase;
import de.zabuza.beedlebot.databridge.EState;
import de.zabuza.beedlebot.databridge.ItemEntry;
import de.zabuza.beedlebot.service.Service;
import de.zabuza.beedlebot.service.routine.Routine;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.sparkle.freewar.IFreewarInstance;
import de.zabuza.sparkle.freewar.inventory.IInventory;
import de.zabuza.sparkle.freewar.player.IPlayer;

public final class PushDataService {

	private static final int WAITING_TIME_MIN = 10;
	private final DataBridge mDataBridge;
	private final IFreewarInstance mInstance;
	private Routine mRoutine;
	private final Service mService;

	public PushDataService(final Service service, final IFreewarInstance instance, final DataBridge dataBridge) {
		mService = service;
		mInstance = instance;
		mDataBridge = dataBridge;

		mRoutine = null;
	}

	public void linkRoutine(final Routine routine) {
		mRoutine = routine;
	}

	public void setBeedleBotServing(final boolean isBeedleBotServing) {
		mDataBridge.setBeedleBotServing(isBeedleBotServing);
	}

	public void update() {
		updateActiveData();
		updatePassiveData();
	}

	public void updateActiveData() {
		if (mRoutine == null) {
			return;
		}

		final boolean isActive = !mService.isPaused() && mService.isActive();

		// Push active flag
		mDataBridge.setActive(isActive);

		// Determine phase
		EPhase phase = mRoutine.getPhase();

		// Determine state
		final EState state;
		if (mService.hasProblem()) {
			state = EState.PROBLEM;
			phase = EPhase.AWAITING_DELIVERY;
			mDataBridge.setProblem(mService.getProblem(), System.currentTimeMillis());
		} else {
			mDataBridge.clearProblem();
			if (!isActive) {
				state = EState.INACTIVE;
				phase = EPhase.AWAITING_DELIVERY;
			} else if (phase == EPhase.AWAITING_DELIVERY) {
				state = EState.STANDBY;
			} else {
				state = EState.ACTIVE;
			}
		}

		mDataBridge.setState(state);
		mDataBridge.setPhase(phase);
	}

	public void updatePassiveData() {
		if (mRoutine == null) {
			return;
		}

		try {
			final IPlayer player = mInstance.getPlayer();
			final IInventory inventory = mInstance.getInventory();

			// Update heartbeat
			mDataBridge.updateHeartBeat();

			// Get lifepoints
			mDataBridge.setCurrentLifepoints(player.getLifePoints());
			mDataBridge.setMaxLifepoints(player.getMaxLifePoints());

			// Get gold
			mDataBridge.setGold(player.getGold());

			// Get inventory size
			final int speed = player.getSpeed();
			final int inventorySize = inventory.getInventorySize();
			mDataBridge.setMaxInventorySize(speed);
			mDataBridge.setInventorySize(inventorySize);

			// Compute waiting time
			final int itemsMoreThanSpeed = inventorySize - speed;
			final int waitingTime;
			if (itemsMoreThanSpeed > 0) {
				waitingTime = WAITING_TIME_MIN + itemsMoreThanSpeed;
			} else {
				waitingTime = WAITING_TIME_MIN;
			}
			mDataBridge.setWaitingTime(waitingTime);

			// Push item-entries
			final Queue<Item> boughtItems = mRoutine.fetchBoughtItems();
			for (final Item item : boughtItems) {
				mDataBridge.pushItemEntry(new ItemEntry(item.getName(), item.getCost(), item.getProfit(),
						item.getStorePriceData().isCached(), item.isConsideredForShop()));
			}

			// Get total cost
			mDataBridge.setTotalCost(mRoutine.getTotalCost());

			// Get total profit
			mDataBridge.setTotalProfit(mRoutine.getTotalProfit());
		} catch (final NoSuchElementException | StaleElementReferenceException | TimeoutException e) {
			// Frame seems to have changed its content. Maybe the player
			// interacted with it. Simply ignore the problem and yield this
			// iteration, hoping it will resolve automatically.
		}
	}
}
