package de.zabuza.beedlebot.service.routine;

import de.zabuza.beedlebot.databridge.EPhase;
import de.zabuza.beedlebot.service.BeedleService;
import de.zabuza.sparkle.freewar.IFreewarInstance;

public final class BeedleRoutine {

	private final IFreewarInstance mInstance;
	private EPhase mPhase;
	private final BeedleService mService;

	public BeedleRoutine(final BeedleService service, final IFreewarInstance instance) {
		mService = service;
		mInstance = instance;
		mPhase = EPhase.ANALYSE;
	}

	public EPhase getPhase() {
		return mPhase;
	}

	public void update() {
		// TODO Remove debug
		System.out.println("Routine");

		// Analyse phase
		if (mPhase == EPhase.ANALYSE) {
			// TODO Implement
			return;
		}

		// Purchase phase
		if (mPhase == EPhase.PURCHASE) {
			// TODO Implement
			return;
		}

		// Wait phase
		if (mPhase == EPhase.WAIT) {
			// TODO Implement
			return;
		}

		// Awaiting delivery phase
		if (mPhase == EPhase.AWAITING_DELIVERY) {
			// TODO Implement
			return;
		}
	}
}
