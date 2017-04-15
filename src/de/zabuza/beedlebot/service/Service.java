package de.zabuza.beedlebot.service;

import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.databridge.io.FetchDataService;
import de.zabuza.beedlebot.databridge.io.PushDataService;
import de.zabuza.beedlebot.service.routine.Routine;
import de.zabuza.beedlebot.store.Store;
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
	private IFreewarInstance mInstance;
	private final BeedleBot mParent;
	private boolean mPaused;
	private Exception mProblem;
	private PushDataService mPushDataService;
	private Routine mRoutine;
	private boolean mShouldStopService;

	private final Store mStore;

	public Service(final IFreewarAPI api, final IFreewarInstance instance, final WebDriver driver, final Store store,
			final BeedleBot parent) {
		mApi = api;
		mInstance = instance;
		mDriver = driver;
		mStore = store;
		mParent = parent;

		mFetchDataService = null;
		mPushDataService = null;
		mRoutine = null;

		mDoRun = true;
		mShouldStopService = false;
		mPaused = true;
		mProblem = null;

		lastUpdateMillis = 0;
	}

	public boolean hasProblem() {
		return mProblem != null;
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
		boolean terminateParent = false;
		try {
			// Create and link the routine
			mRoutine = new Routine(this, mInstance, mDriver, mPushDataService, mStore);
			mPushDataService.linkRoutine(mRoutine);
			mPushDataService.setBeedleBotServing(true);
		} catch (final Exception e1) {
			// TODO Error logging
			// Do not enter the service loop
			mDoRun = false;
			try {
				mPushDataService.setBeedleBotServing(false);
			} catch (final Exception e2) {
				// TODO Error logging
			}
			terminateParent = true;
		}

		// Enter the service loop
		while (mDoRun) {
			try {
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
						// Continue from pause
						mPaused = false;
						// Clear the problem flag
						clearProblem();
						mFetchDataService.clearStartSignal();
						mPushDataService.updateActiveData();
						// TODO Remove debug
						System.out.println("Continued.");
					}
					if (!mPaused && (hasProblem() || mFetchDataService.isStopSignalSet())) {
						// Pause
						mPaused = true;
						mFetchDataService.clearStopSignal();
						mRoutine.reset();
						mPushDataService.updateActiveData();
						// TODO Remove debug
						System.out.println("Paused.");
					}
				}
				if (mShouldStopService) {
					mDoRun = false;
					mPaused = true;
					mRoutine.reset();
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
			} catch (final Exception e1) {
				// TODO Correct error logging, do not use error console
				e1.printStackTrace();
				// Try to shutdown
				mDoRun = false;
				mPaused = true;
				mRoutine.reset();
				try {
					mPushDataService.setBeedleBotServing(false);
				} catch (final Exception e2) {
					// TODO Error logging
				}
				terminateParent = true;
			}
		}

		// If the service is leaved shut it down
		shutdown();

		// Request parent to terminate
		if (terminateParent) {
			mParent.shutdown();
		}
	}

	public void setProblem(final Exception problem) {
		// TODO Error logging, push problem to data bridge
		// TODO Remove debug print to error log
		problem.printStackTrace(System.out);
		mProblem = problem;
	}

	public void stopService() {
		mShouldStopService = true;
	}

	public void waitIteration() {
		try {
			sleep(SERVICE_INTERVAL);
		} catch (final InterruptedException e) {
			// TODO Error logging
			// Log the error but continue
		}
	}

	private void clearProblem() {
		mProblem = null;
	}

	private void shutdown() {
		if (mApi != null) {
			if (mInstance != null) {
				try {
					mApi.logout(mInstance);
				} catch (final Exception e) {
					// TODO Error logging
					// Log the error but continue
				}
				mInstance = null;
			}
			try {
				mApi.shutdown();
			} catch (final Exception e) {
				// TODO Error logging
				// Log the error but continue
			}

		}
		// TODO Remove debug
		System.out.println("Service shut down");
	}
}
