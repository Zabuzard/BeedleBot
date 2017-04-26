package de.zabuza.beedlebot.service;

import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.databridge.io.FetchDataService;
import de.zabuza.beedlebot.databridge.io.PushDataService;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.logging.LoggerUtil;
import de.zabuza.beedlebot.service.routine.Routine;
import de.zabuza.beedlebot.store.Store;
import de.zabuza.sparkle.IFreewarAPI;
import de.zabuza.sparkle.freewar.IFreewarInstance;

/**
 * Actual service thread of the tool. Once a connection to Freewar was
 * established this service enters a loop that analyzes, purchases and sells
 * items from the central traders depot. Meanwhile it communicates with the web
 * interface over the {@link DataBridge}. Call {@link #start()} to start the
 * service and {@link #stopService()} to stop it. Prior to starting the methods
 * {@link #registerFetchDataService(FetchDataService)} and
 * {@link #registerPushDataService(PushDataService)} needs to be called. If the
 * service leaves its life cycle abnormally it will request the parent tool to
 * also shutdown.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class Service extends Thread {
	/**
	 * The time in milliseconds to wait for the next iteration of the life
	 * cycle.
	 */
	private final static long SERVICE_INTERVAL = 100;
	/**
	 * The time in milliseconds to wait for the next data push and fetch cycle.
	 */
	private final static long UPDATE_INTERVAL = 500;

	/**
	 * The timestamp of the last data push and fetch cycle.
	 */
	private long lastUpdateMillis;
	/**
	 * The Freewar API to use for accessing the games contents.
	 */
	private final IFreewarAPI mApi;
	/**
	 * Internal flag whether the service should run or not. If set to
	 * <tt>false</tt> the service will not enter the next iteration of its life
	 * cycle and shutdown.
	 */
	private boolean mDoRun;
	/**
	 * The driver to use for accessing browsers contents.
	 */
	private final WebDriver mDriver;
	/**
	 * The service to use for fetching data from the {@link DataBridge}.
	 */
	private FetchDataService mFetchDataService;
	/**
	 * The instance of the Freewar API to use for accessing the games contents.
	 */
	private final IFreewarInstance mInstance;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;
	/**
	 * The parent object that controls the service. If the service shuts down it
	 * will request its parent to also shutdown.
	 */
	private final BeedleBot mParent;
	/**
	 * Whether the service is paused or not. If paused it will stop updating the
	 * main routine but continue with fetching and pushing data to the
	 * {@link DataBridge}.
	 */
	private boolean mPaused;
	/**
	 * Set if the service encountered a problem that needs to be resolved,
	 * <tt>null</tt> else. Use {@link #setProblem(Exception)} to set it and
	 * {@link #clearProblem()} to clear it. Use {@link #hasProblem()} to ask if
	 * there is currently a problem and {@link #getProblemTimestamp()} to get a
	 * timestamp of the last encountered problem.
	 */
	private Exception mProblem;
	/**
	 * The timestamp of the last encountered problem. Use
	 * {@link #setProblem(Exception)} to set a problem and
	 * {@link #clearProblem()} to clear it. Use {@link #hasProblem()} to ask if
	 * there is currently a problem and {@link #getProblemTimestamp()} to get a
	 * timestamp of the last encountered problem.
	 */
	private long mProblemTimestamp;
	/**
	 * The service to use for pushing data to the {@link DataBridge}.
	 */
	private PushDataService mPushDataService;
	/**
	 * The actual routine of the service which analyzes, purchases and sells
	 * items from the central traders depot.
	 */
	private Routine mRoutine;
	/**
	 * Whether the service should stop or not. If set to <tt>true</tt> the
	 * service will try to leave its life cycle in a normal way and shutdown.
	 */
	private boolean mShouldStopService;
	/**
	 * The store to use for retrieving information about item prices.
	 */
	private final Store mStore;

	/**
	 * Creates a new Service instance. Call {@link #start()} to start the
	 * service and {@link #stopService()} to stop it. Prior to starting the
	 * methods {@link #registerFetchDataService(FetchDataService)} and
	 * {@link #registerPushDataService(PushDataService)} needs to be called.
	 * 
	 * @param api
	 *            The Freewar API to use for accessing the games contents
	 * @param instance
	 *            The instance of the Freewar API to use for accessing the games
	 *            contents
	 * @param driver
	 *            The driver to use for accessing browsers contents
	 * @param store
	 *            The store to use for retrieving information about item prices
	 * @param parent
	 *            The parent object that controls the service. If the service
	 *            shuts down in an abnormal way it will request its parent to
	 *            also shutdown.
	 */
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

	/**
	 * Gets the current encountered problem if set. It is set if the service
	 * encountered a problem that needs to be resolved, <tt>null</tt> else. Use
	 * {@link #setProblem(Exception)} to set it and {@link #clearProblem()} to
	 * clear it. Use {@link #hasProblem()} to ask if there is currently a
	 * problem and {@link #getProblemTimestamp()} to get a timestamp of the last
	 * encountered problem.
	 * 
	 * @return The current encountered problem or <tt>null</tt> if there is no
	 */
	public Exception getProblem() {
		return this.mProblem;
	}

	/**
	 * The timestamp of the last encountered problem. Use
	 * {@link #setProblem(Exception)} to set a problem and
	 * {@link #clearProblem()} to clear it. Use {@link #hasProblem()} to ask if
	 * there is currently a problem and {@link #getProblem()} to actually get
	 * it.
	 * 
	 * @return The timestamp of the last encountered problem
	 */
	public long getProblemTimestamp() {
		return this.mProblemTimestamp;
	}

	/**
	 * Whether the service currently encountered a problem that needs to be
	 * resolved. A problem can be set by using {@link #setProblem(Exception)}
	 * and accessed by {@link #getProblem()} and {@link #clearProblem()}.
	 * 
	 * @return <tt>True</tt> if there is a problem, <tt>false</tt> if not.
	 */
	public boolean hasProblem() {
		return this.mProblem != null;
	}

	/**
	 * Whether the service is alive and running.
	 * 
	 * @return <tt>True</tt> if the service is alive and running, <tt>false</tt>
	 *         otherwise
	 */
	public boolean isActive() {
		return this.mDoRun;
	}

	/**
	 * Whether the service is paused or not. If paused it will stop updating the
	 * main routine but continue with fetching and pushing data to the
	 * {@link DataBridge}.
	 * 
	 * @return <tt>True</tt> if the service is paused, <tt>false</tt> if not
	 */
	public boolean isPaused() {
		return this.mPaused;
	}

	/**
	 * Registers the service to use for fetching data from the
	 * {@link DataBridge}.
	 * 
	 * @param fetchDataService
	 *            The service to use for fetching data from the
	 *            {@link DataBridge}
	 */
	public void registerFetchDataService(final FetchDataService fetchDataService) {
		this.mFetchDataService = fetchDataService;
	}

	/**
	 * Registers the service to use for pushing data to the {@link DataBridge}.
	 * 
	 * @param pushDataService
	 *            The service to use for pushing data to the {@link DataBridge}
	 */
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

		// Enter the life cycle
		while (this.mDoRun) {
			try {
				if (this.mFetchDataService == null || this.mPushDataService == null) {
					waitToNextIteration();
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

						// Flush log and save the store
						this.mLogger.flush();
						this.mStore.save();
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
				waitToNextIteration();
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

	/**
	 * Set if the service encountered a problem that needs to be resolved. Use
	 * {@link #getProblem()} to get it and {@link #clearProblem()} to clear it.
	 * Use {@link #hasProblem()} to ask if there is currently a problem and
	 * {@link #getProblemTimestamp()} to get a timestamp of the last encountered
	 * problem.
	 * 
	 * @param problem
	 *            The problem to set
	 */
	public void setProblem(final Exception problem) {
		this.mProblem = problem;
		this.mProblemTimestamp = System.currentTimeMillis();
		this.mLogger.logError("Problem registered: " + LoggerUtil.getStackTrace(problem));
		this.mLogger.flush();

		this.mPushDataService.updateActiveData();
	}

	/**
	 * Requests the service to stop. It will try to end its life cycle in a
	 * normal way and shutdown.
	 */
	public void stopService() {
		this.mShouldStopService = true;
	}

	/**
	 * Waits a given time before executing the next iteration of the services
	 * life cycle.
	 */
	public void waitToNextIteration() {
		try {
			sleep(SERVICE_INTERVAL);
		} catch (final InterruptedException e) {
			// Log the error but continue
			this.mLogger.logError("Service wait got interrupted: " + LoggerUtil.getStackTrace(e));
		}
	}

	/**
	 * If the service encountered a problem that needs to be resolved this
	 * method will clear this problem, i.e. setting it to resolved. Use
	 * {@link #setProblem(Exception)} to set a problem and {@link #getProblem()}
	 * to get it. Use {@link #hasProblem()} to ask if there is currently a
	 * problem and {@link #getProblemTimestamp()} to get a timestamp of the last
	 * encountered problem.
	 */
	private void clearProblem() {
		this.mProblem = null;
	}

	/**
	 * Shuts the service down. Afterwards this instance can not be used anymore,
	 * instead create a new one.
	 */
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
