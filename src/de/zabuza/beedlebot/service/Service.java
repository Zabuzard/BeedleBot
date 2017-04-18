package de.zabuza.beedlebot.service;

import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.databridge.io.FetchDataService;
import de.zabuza.beedlebot.databridge.io.PushDataService;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.logging.LoggerUtil;
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
	private final ILogger mLogger;
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
		mLogger = LoggerFactory.getLogger();

		mFetchDataService = null;
		mPushDataService = null;
		mRoutine = null;

		mDoRun = true;
		mShouldStopService = false;
		mPaused = true;
		mProblem = null;

		lastUpdateMillis = 0;
	}

	public Exception getProblem() {
		return mProblem;
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
			// Do not enter the service loop
			mLogger.logError("Error while starting service, not entering: " + LoggerUtil.getStackTrace(e1));
			mDoRun = false;
			try {
				mPushDataService.setBeedleBotServing(false);
			} catch (final Exception e2) {
				mLogger.logError("Error while starting service, not entering: " + LoggerUtil.getStackTrace(e2));
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

						mLogger.logInfo("Continuing service");
					}
					if (!mPaused && (hasProblem() || mFetchDataService.isStopSignalSet())) {
						// Pause
						mPaused = true;
						mFetchDataService.clearStopSignal();
						mRoutine.reset();
						mPushDataService.updateActiveData();

						mLogger.logInfo("Pause service");
						mLogger.flush();
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
				mLogger.logError("Error while running service, shutting down: " + LoggerUtil.getStackTrace(e1));
				// Try to shutdown
				mDoRun = false;
				mPaused = true;
				mRoutine.reset();
				try {
					mPushDataService.setBeedleBotServing(false);
				} catch (final Exception e2) {
					mLogger.logError("Error while trying to shutdown service: " + LoggerUtil.getStackTrace(e2));
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
		mProblem = problem;
		mLogger.logError("Problem registered: " + LoggerUtil.getStackTrace(problem));
		mLogger.flush();

		mPushDataService.updateActiveData();
	}

	public void stopService() {
		mShouldStopService = true;
	}

	public void waitIteration() {
		try {
			sleep(SERVICE_INTERVAL);
		} catch (final InterruptedException e) {
			// Log the error but continue
			mLogger.logError("Service wait got interrupted: " + LoggerUtil.getStackTrace(e));
		}
	}

	private void clearProblem() {
		mProblem = null;
	}

	private void shutdown() {
		mLogger.logInfo("Shutting down service");
		if (mApi != null) {
			try {
				mApi.shutdown(false);
			} catch (final Exception e) {
				// Log the error but continue
				mLogger.logError("Error while shutting down API: " + LoggerUtil.getStackTrace(e));
			}

		}
	}
}
