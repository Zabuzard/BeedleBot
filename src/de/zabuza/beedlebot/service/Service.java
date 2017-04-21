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
	private long mProblemTimestamp;
	private PushDataService mPushDataService;
	private Routine mRoutine;

	private boolean mShouldStopService;
	private final Store mStore;

	public Service(final IFreewarAPI api, final IFreewarInstance instance, final WebDriver driver, final Store store,
			final BeedleBot parent) {
		this.mApi = api;
		this.mInstance = instance;
		this.mDriver = driver;
		this.mStore = store;
		this.mParent = parent;
		this.mLogger = LoggerFactory.getLogger();

		this.mFetchDataService = null;
		this.mPushDataService = null;
		this.mRoutine = null;

		this.mDoRun = true;
		this.mShouldStopService = false;
		this.mPaused = true;
		this.mProblem = null;

		this.lastUpdateMillis = 0;
		this.mProblemTimestamp = 0;
	}

	public Exception getProblem() {
		return this.mProblem;
	}

	public long getProblemTimestamp() {
		return this.mProblemTimestamp;
	}

	public boolean hasProblem() {
		return this.mProblem != null;
	}

	public boolean isActive() {
		return this.mDoRun;
	}

	public boolean isPaused() {
		return this.mPaused;
	}

	public void registerFetchDataService(final FetchDataService fetchDataService) {
		this.mFetchDataService = fetchDataService;
	}

	public void registerPushDataService(final PushDataService pushDataService) {
		this.mPushDataService = pushDataService;
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
			this.mRoutine = new Routine(this, this.mInstance, this.mDriver, this.mPushDataService, this.mStore);
			this.mPushDataService.linkRoutine(this.mRoutine);
			this.mPushDataService.setBeedleBotServing(true);
		} catch (final Exception e1) {
			// Do not enter the service loop
			this.mLogger.logError("Error while starting service, not entering: " + LoggerUtil.getStackTrace(e1));
			this.mDoRun = false;
			try {
				this.mPushDataService.setBeedleBotServing(false);
			} catch (final Exception e2) {
				this.mLogger.logError("Error while starting service, not entering: " + LoggerUtil.getStackTrace(e2));
			}
			terminateParent = true;
		}

		// Enter the service loop
		while (this.mDoRun) {
			try {
				if (this.mFetchDataService == null || this.mPushDataService == null) {
					waitIteration();
					continue;
				}

				// Determine if to update services
				final boolean doUpdate;
				final long currentMillis = System.currentTimeMillis();
				if (currentMillis - this.lastUpdateMillis >= UPDATE_INTERVAL) {
					this.lastUpdateMillis = currentMillis;
					doUpdate = true;
				} else {
					doUpdate = false;
				}

				// Fetch data
				if (doUpdate) {
					this.mFetchDataService.update();
				}

				// Check signals
				if (doUpdate) {
					if (this.mPaused && this.mFetchDataService.isStartSignalSet()) {
						// Continue from pause
						this.mPaused = false;
						// Clear the problem flag
						clearProblem();
						this.mFetchDataService.clearStartSignal();
						this.mPushDataService.updateActiveData();

						this.mLogger.logInfo("Continuing service");
					}
					if (!this.mPaused && (hasProblem() || this.mFetchDataService.isStopSignalSet())) {
						// Pause
						this.mPaused = true;
						this.mFetchDataService.clearStopSignal();
						this.mRoutine.reset();
						this.mPushDataService.updateActiveData();

						this.mLogger.logInfo("Pause service");
						this.mLogger.flush();
					}
				}
				if (this.mShouldStopService) {
					this.mDoRun = false;
					this.mPaused = true;
					this.mRoutine.reset();
					this.mPushDataService.setBeedleBotServing(false);
				}

				// Continue routine
				if (!this.mPaused) {
					this.mRoutine.update();
				}

				// Push data
				if (doUpdate) {
					this.mPushDataService.update();
				}

				// Delay the next iteration
				waitIteration();
			} catch (final Exception e1) {
				this.mLogger.logError("Error while running service, shutting down: " + LoggerUtil.getStackTrace(e1));
				// Try to shutdown
				this.mDoRun = false;
				this.mPaused = true;
				this.mRoutine.reset();
				try {
					this.mPushDataService.setBeedleBotServing(false);
				} catch (final Exception e2) {
					this.mLogger.logError("Error while trying to shutdown service: " + LoggerUtil.getStackTrace(e2));
				}
				terminateParent = true;
			}
		}

		// If the service is leaved shut it down
		shutdown();

		// Request parent to terminate
		if (terminateParent) {
			this.mParent.shutdown();
		}
	}

	public void setProblem(final Exception problem) {
		this.mProblem = problem;
		this.mProblemTimestamp = System.currentTimeMillis();
		this.mLogger.logError("Problem registered: " + LoggerUtil.getStackTrace(problem));
		this.mLogger.flush();

		this.mPushDataService.updateActiveData();
	}

	public void stopService() {
		this.mShouldStopService = true;
	}

	public void waitIteration() {
		try {
			sleep(SERVICE_INTERVAL);
		} catch (final InterruptedException e) {
			// Log the error but continue
			this.mLogger.logError("Service wait got interrupted: " + LoggerUtil.getStackTrace(e));
		}
	}

	private void clearProblem() {
		this.mProblem = null;
	}

	private void shutdown() {
		this.mLogger.logInfo("Shutting down service");
		if (this.mApi != null) {
			try {
				this.mApi.shutdown(false);
			} catch (final Exception e) {
				// Log the error but continue
				this.mLogger.logError("Error while shutting down API: " + LoggerUtil.getStackTrace(e));
			}

		}
	}
}
