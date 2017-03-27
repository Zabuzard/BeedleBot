package de.zabuza.beedlebot.service;

import de.zabuza.beedlebot.databridge.io.FetchDataService;
import de.zabuza.beedlebot.databridge.io.PushDataService;
import de.zabuza.sparkle.IFreewarAPI;
import de.zabuza.sparkle.freewar.IFreewarInstance;

public final class BeedleService extends Thread {

	private final static long SERVICE_INTERVAL = 100;
	private final static long UPDATE_INTERVAL = 500;

	private final IFreewarAPI mApi;
	private IFreewarInstance mInstance;
	private PushDataService mPushDataService;
	private FetchDataService mFetchDataService;
	private boolean mDoRun;
	private boolean mShouldStopService;
	private boolean mPaused;

	private long accumulatedServiceInterval;

	public BeedleService(final IFreewarAPI api, final IFreewarInstance instance) {
		mApi = api;
		mInstance = instance;
		mFetchDataService = null;
		mPushDataService = null;

		mDoRun = true;
		mShouldStopService = false;
		mPaused = false;

		accumulatedServiceInterval = 0;
	}

	public void registerPushDataService(final PushDataService pushDataService) {
		mPushDataService = pushDataService;
	}

	public void registerFetchDataService(final FetchDataService fetchDataService) {
		mFetchDataService = fetchDataService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (mDoRun) {
			if (mFetchDataService == null || mPushDataService == null) {
				waitIteration();
				continue;
			}

			// Determine if to update services
			final boolean doUpdate;
			if (accumulatedServiceInterval >= UPDATE_INTERVAL) {
				accumulatedServiceInterval = 0;
				doUpdate = true;
			} else {
				doUpdate = false;
			}

			// Fetch data
			if (doUpdate) {
				mFetchDataService.update();
				// TODO Remove debug
				System.out.println("Fetched");
			}

			// Check signals
			if (doUpdate) {
				if (mPaused && mFetchDataService.isStartSignalSet()) {
					mPaused = false;
					mFetchDataService.clearStartSignal();
					// TODO Remove debug
					System.out.println("Continued");
				}
				if (!mPaused && mFetchDataService.isStopSignalSet()) {
					mPaused = true;
					mFetchDataService.clearStopSignal();
					// TODO Remove debug
					System.out.println("Paused");
				}
			}
			if (mShouldStopService) {
				mDoRun = false;
				mPaused = true;
				// TODO Remove debug
				System.out.println("Stopping service");
			}

			// Continue service
			if (!mPaused) {
				// TODO Implement
				System.out.println("Serving");
			}

			// Push data
			if (doUpdate) {
				mPushDataService.update();
				// TODO Remove debug
				System.out.println("Pushed");
			}

			waitIteration();
		}

		shutdown();
	}

	private void shutdown() {
		if (mApi != null) {
			if (mInstance != null) {
				mApi.logout(mInstance);
				mInstance = null;
			}
			mApi.shutdown();
		}
		// TODO Remove debug
		System.out.println("Service shut down");
	}

	public boolean isPaused() {
		return mPaused;
	}

	public void waitIteration() {
		try {
			accumulatedServiceInterval += SERVICE_INTERVAL;
			sleep(SERVICE_INTERVAL);
		} catch (InterruptedException e) {
			// TODO Correct error handling and logging
			e.printStackTrace();
		}
	}

	public boolean isActive() {
		return mDoRun;
	}

	public void stopService() {
		mShouldStopService = true;
	}
}
