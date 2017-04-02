package de.zabuza.beedlebot.service;

import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.databridge.io.FetchDataService;
import de.zabuza.beedlebot.databridge.io.PushDataService;
import de.zabuza.beedlebot.service.routine.Routine;
import de.zabuza.sparkle.IFreewarAPI;
import de.zabuza.sparkle.freewar.IFreewarInstance;

public final class Service extends Thread {

	private final static long SERVICE_INTERVAL = 100;
	private final static long UPDATE_INTERVAL = 500;

	private long lastUpdateMillis;
	private final IFreewarAPI mApi;
	private boolean mDoRun;
	private final WebDriver mDriver;
	private FetchDataService mFetchDataService;
	private boolean mHasProblem;
	private IFreewarInstance mInstance;
	private boolean mPaused;
	private PushDataService mPushDataService;
	private Routine mRoutine;
	private boolean mShouldStopService;

	public Service(final IFreewarAPI api, final IFreewarInstance instance, WebDriver driver) {
		mApi = api;
		mInstance = instance;
		mDriver = driver;
		mFetchDataService = null;
		mPushDataService = null;
		mRoutine = null;

		mDoRun = true;
		mShouldStopService = false;
		mPaused = true;
		mHasProblem = false;

		lastUpdateMillis = 0;
	}

	public boolean hasProblem() {
		return mHasProblem;
	}

	public boolean isActive() {
		return mDoRun;
	}

	public boolean isPaused() {
		return mPaused;
	}

	public void registerFetchDataService(final FetchDataService fetchDataService) {
		mFetchDataService = fetchDataService;
	}

	public void registerPushDataService(final PushDataService pushDataService) {
		mPushDataService = pushDataService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// Create and link the routine
		mRoutine = new Routine(this, mInstance, mDriver, mPushDataService);
		mPushDataService.linkRoutine(mRoutine);

		// Enter the service loop
		mPushDataService.setBeedleBotServing(true);
		while (mDoRun) {
			if (mFetchDataService == null || mPushDataService == null) {
				waitIteration();
				continue;
			}

			// Determine if to update services
			final boolean doUpdate;
			final long currentMillis = System.currentTimeMillis();
			if (currentMillis - lastUpdateMillis >= UPDATE_INTERVAL) {
				lastUpdateMillis = currentMillis;
				doUpdate = true;
			} else {
				doUpdate = false;
			}

			// Fetch data
			if (doUpdate) {
				mFetchDataService.update();
			}

			// Check signals
			if (doUpdate) {
				if (mPaused && mFetchDataService.isStartSignalSet()) {
					mPaused = false;
					mFetchDataService.clearStartSignal();
					mPushDataService.updateActiveData();
					// TODO Remove debug
					System.out.println("Continued.");
				}
				if (!mPaused && mFetchDataService.isStopSignalSet()) {
					mPaused = true;
					mFetchDataService.clearStopSignal();
					mPushDataService.updateActiveData();
					// TODO Remove debug
					System.out.println("Paused.");
				}
			}
			if (mShouldStopService) {
				mDoRun = false;
				mPaused = true;
				mPushDataService.setBeedleBotServing(false);
			}

			// Continue routine
			if (!mPaused) {
				mRoutine.update();
			}

			// Push data
			if (doUpdate) {
				mPushDataService.update();
			}

			// Delay the next iteration
			waitIteration();
		}

		// If the service is leaved shut it down
		shutdown();
	}

	public void setHasProblem(final boolean hasProblem) {
		mHasProblem = hasProblem;
	}

	public void stopService() {
		mShouldStopService = true;
	}

	public void waitIteration() {
		try {
			sleep(SERVICE_INTERVAL);
		} catch (InterruptedException e) {
			// TODO Correct error handling and logging
			e.printStackTrace();
		}
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
}
