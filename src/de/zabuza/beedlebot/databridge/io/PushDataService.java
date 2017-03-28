package de.zabuza.beedlebot.databridge.io;

import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.databridge.EPhase;
import de.zabuza.beedlebot.databridge.EState;
import de.zabuza.beedlebot.service.Service;
import de.zabuza.beedlebot.service.routine.Routine;
import de.zabuza.sparkle.freewar.IFreewarInstance;
import de.zabuza.sparkle.freewar.player.IPlayer;

public final class PushDataService {

	private final Service mService;
	private final DataBridge mDataBridge;
	private final IFreewarInstance mInstance;
	private Routine mRoutine;

	public PushDataService(final Service service, final IFreewarInstance instance,
			final DataBridge dataBridge) {
		mService = service;
		mInstance = instance;
		mDataBridge = dataBridge;

		mRoutine = null;
	}

	public void linkRoutine(final Routine routine) {
		mRoutine = routine;
	}

	public void update() {
		if (mRoutine == null) {
			return;
		}

		final boolean isActive = mService.isPaused() || !mService.isActive();

		// Push active flag
		mDataBridge.setActive(isActive);

		// Determine phase
		final EPhase phase = mRoutine.getPhase();
		mDataBridge.setPhase(phase);

		// Determine state
		final EState state;
		if (isActive) {
			state = EState.INACTIVE;
		} else if (mService.hasProblem()) {
			state = EState.PROBLEM;
		} else if (phase == EPhase.AWAITING_DELIVERY) {
			state = EState.STANDBY;
		} else {
			state = EState.ACTIVE;
		}
		mDataBridge.setState(state);

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

		// TODO Implement item-entry-push and totalCost, totalProfit
	}
}
