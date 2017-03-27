package de.zabuza.beedlebot.databridge.io;

import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.service.BeedleService;
import de.zabuza.sparkle.freewar.IFreewarInstance;

public final class PushDataService {

	private final BeedleService mBeedleService;
	private final IFreewarInstance mInstance;
	private final DataBridge mDataBridge;

	public PushDataService(final BeedleService beedleService, final IFreewarInstance instance,
			final DataBridge dataBridge) {
		mBeedleService = beedleService;
		mInstance = instance;
		mDataBridge = dataBridge;
	}

	public void update() {
		// TODO Implement
	}
}
