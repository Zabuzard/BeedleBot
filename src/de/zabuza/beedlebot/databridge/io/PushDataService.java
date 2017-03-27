package de.zabuza.beedlebot.databridge.io;

import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.databridge.EPhase;
import de.zabuza.beedlebot.databridge.EState;
import de.zabuza.beedlebot.service.BeedleService;
import de.zabuza.beedlebot.service.routine.BeedleRoutine;
import de.zabuza.sparkle.freewar.IFreewarInstance;
import de.zabuza.sparkle.freewar.player.IPlayer;

public final class PushDataService {

	private final BeedleService mBeedleService;
	private final DataBridge mDataBridge;
	private final IFreewarInstance mInstance;
	private BeedleRoutine mRoutine;

	public PushDataService(final BeedleService beedleService, final IFreewarInstance instance,
			final DataBridge dataBridge) {
		mBeedleService = beedleService;
		mInstance = instance;
		mDataBridge = dataBridge;

		mRoutine = null;
	}

	public void linkRoutine(final BeedleRoutine routine) {
		mRoutine = routine;
	}

	public void update() {
		if (mRoutine == null) {
			return;
		}

		final boolean isActive = mBeedleService.isPaused() || !mBeedleService.isActive();

		// Push active flag
		mDataBridge.setActive(isActive);

		// Determine phase
		final EPhase phase = mRoutine.getPhase();
		mDataBridge.setPhase(phase);

		// Determine state
		final EState state;
		if (isActive) {
			state = EState.INACTIVE;
		} else if (mBeedleService.hasProblem()) {
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
