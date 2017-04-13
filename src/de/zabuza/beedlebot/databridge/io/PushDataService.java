package de.zabuza.beedlebot.databridge.io;

import java.util.Queue;

import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.databridge.EPhase;
import de.zabuza.beedlebot.databridge.EState;
import de.zabuza.beedlebot.databridge.ItemEntry;
import de.zabuza.beedlebot.service.Service;
import de.zabuza.beedlebot.service.routine.Routine;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.sparkle.freewar.IFreewarInstance;
import de.zabuza.sparkle.freewar.player.IPlayer;

public final class PushDataService {

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
		if (!isActive) {
			state = EState.INACTIVE;
			phase = EPhase.AWAITING_DELIVERY;
		} else if (mService.hasProblem()) {
			state = EState.PROBLEM;
		} else if (phase == EPhase.AWAITING_DELIVERY) {
			state = EState.STANDBY;
		} else {
			state = EState.ACTIVE;
		}
		mDataBridge.setState(state);
		mDataBridge.setPhase(phase);
	}

	public void updatePassiveData() {
		if (mRoutine == null) {
			return;
		}

		// Get lifepoints
		final IPlayer player = mInstance.getPlayer();
		mDataBridge.setCurrentLifepoints(player.getLifePoints());
		mDataBridge.setMaxLifepoints(player.getMaxLifePoints());

		// Get gold
		mDataBridge.setGold(player.getGold());

		// Get inventory size
		mDataBridge.setMaxInventorySize(player.getSpeed());
		// TODO Implement size of inventory in Sparkle
		mDataBridge.setInventorySize(0);

		// TODO Compute waiting time
		mDataBridge.setWaitingTime(0);

		// Push item-entries
		final Queue<Item> boughtItems = mRoutine.fetchBoughtItems();
		for (final Item item : boughtItems) {
			mDataBridge.pushItemEntry(new ItemEntry(item.getName(), item.getCost(), item.getProfit()));
		}

		// Get total cost
		mDataBridge.setTotalCost(mRoutine.getTotalCost());

		// Get total profit
		mDataBridge.setTotalProfit(mRoutine.getTotalProfit());
	}
}
