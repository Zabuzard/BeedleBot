package de.zabuza.beedlebot;

import java.awt.AWTException;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.databridge.io.FetchDataService;
import de.zabuza.beedlebot.databridge.io.PushDataService;
import de.zabuza.beedlebot.exceptions.EmptyUserCredentialsException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.logging.LoggerUtil;
import de.zabuza.beedlebot.logindialog.LoginDialog;
import de.zabuza.beedlebot.logindialog.controller.settings.IBrowserSettingsProvider;
import de.zabuza.beedlebot.logindialog.controller.settings.IUserSettingsProvider;
import de.zabuza.beedlebot.service.Service;
import de.zabuza.beedlebot.store.Store;
import de.zabuza.beedlebot.tray.TrayManager;
import de.zabuza.sparkle.IFreewarAPI;
import de.zabuza.sparkle.Sparkle;
import de.zabuza.sparkle.freewar.EWorld;
import de.zabuza.sparkle.freewar.IFreewarInstance;
import de.zabuza.sparkle.webdriver.EBrowser;
import de.zabuza.sparkle.webdriver.IHasWebDriver;

/**
 * The entry class of the BeedleBot tool. After creation and initialization via
 * {@link #initialize()} the tool can be started by {@link #start()} and ended
 * by {@link #shutdown()} or {@link #stop()}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class BeedleBot {
	/**
	 * The file path to the image of the icon to use.
	 */
	private static final String IMAGE_PATH_ICON = "res/img/icon.png";

	/**
	 * Starts the BeedleBot tool and ensures that all thrown and not caught
	 * exceptions create log messages and shutdown the tool.
	 * 
	 * @param args
	 *            Not supported
	 */
	public static void main(final String[] args) {
		BeedleBot beedleBot = null;
		try {
			beedleBot = new BeedleBot();
			beedleBot.initialize();
			beedleBot.start();
		} catch (final Exception e) {
			LoggerFactory.getLogger().logError("Error, shutting down: " + LoggerUtil.getStackTrace(e));
			// Try to shutdown
			if (beedleBot != null) {
				beedleBot.shutdown();
			}
		}
	}

	/**
	 * The Freewar API to use for creation of Freewar instances.
	 */
	private IFreewarAPI mApi;
	/**
	 * The data bridge to use which is the communication channel of the tool
	 * with its web interface.
	 */
	private DataBridge mDataBridge;
	/**
	 * The driver to use for interaction with the browser.
	 */
	private WebDriver mDriver;
	/**
	 * The fetch data service to use for getting data from the web interface.
	 */
	private FetchDataService mFetchDataService;
	/**
	 * The image of the icon to use.
	 */
	private Image mIconImage;
	/**
	 * The Freewar instance to use for interaction with the game.
	 */
	private IFreewarInstance mInstance;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;
	/**
	 * The login dialog to use prior to service start.
	 */
	private LoginDialog mLoginDialog;
	/**
	 * The push data service to use for setting data to the web interface.
	 */
	private PushDataService mPushDataService;
	/**
	 * The main service of the tool.
	 */
	private Service mService;
	/**
	 * The store to use which holds data for Freewar items.
	 */
	private Store mStore;
	/**
	 * The tray manager to use which manages the tray icon of the tool.
	 */
	private TrayManager mTrayManager;

	/**
	 * Creates a new instance of the tool. After creation call
	 * {@link #initialize()} and then {@link #start()}. To end the tool call
	 * {@link #shutdown()} or {@link #stop()}.
	 */
	public BeedleBot() {
		this.mTrayManager = null;
		this.mLoginDialog = null;
		this.mIconImage = null;
		this.mApi = null;
		this.mInstance = null;
		this.mDriver = null;
		this.mDataBridge = null;
		this.mService = null;
		this.mPushDataService = null;
		this.mFetchDataService = null;
		this.mStore = null;
		this.mLogger = LoggerFactory.getLogger();
	}

	/**
	 * Initializes the tool. Call this method prior to {@link #start()}.
	 * 
	 * @throws IOException
	 *             If an I/O-Exception occurs when reading the icon image
	 * @throws AWTException
	 *             If the desktop system tray is missing
	 */
	public void initialize() throws IOException, AWTException {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Initializing BeedleBot");
		}
		this.mIconImage = ImageIO.read(new File(IMAGE_PATH_ICON));
		this.mTrayManager = new TrayManager(this, this.mIconImage);
		this.mTrayManager.addTrayIcon();
	}

	/**
	 * Shuts the tool down and frees all used resources. The object instance can
	 * not be used anymore after calling this method, instead create a new one.
	 * If the tool should only get restarted consider using {@link #stop()}
	 * instead of this method.
	 */
	public void shutdown() {
		this.mLogger.flush();
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Shutting down BeedleBot");
		}
		try {
			stop();
		} catch (final Exception e) {
			this.mLogger.logError("Error while stopping: " + LoggerUtil.getStackTrace(e));
		}

		if (this.mTrayManager != null) {
			try {
				this.mTrayManager.removeTrayIcon();
			} catch (final Exception e) {
				this.mLogger.logError("Error while removing tray icon: " + LoggerUtil.getStackTrace(e));
			}

		}

		this.mLogger.logInfo("BeedleBot shutdown");
		this.mLogger.close();
	}

	/**
	 * Starts the tool. Prior to this call {@link #initialize()}. To end the
	 * tool call {@link #shutdown()} or {@link #stop()}.
	 */
	public void start() {
		this.mLogger.logInfo("BeedleBot start");

		this.mLoginDialog = new LoginDialog(this, this.mIconImage);
	}

	/**
	 * Starts the actual main service of the tool. The method tries to catch all
	 * not caught exceptions to ensure a proper shutdown of the tool.
	 * 
	 * @param userSettingsProvider
	 *            Object that provides settings about the Freewar user to use
	 *            for the tool
	 * @param browserSettingsProvider
	 *            Object that provides settings about the browser to use for the
	 *            tool
	 * @throws EmptyUserCredentialsException
	 *             If user settings like name or password are empty or
	 *             <tt>null</tt>
	 */
	public void startService(final IUserSettingsProvider userSettingsProvider,
			final IBrowserSettingsProvider browserSettingsProvider) throws EmptyUserCredentialsException {
		try {
			this.mLogger.logInfo("Starting service");

			final String username = userSettingsProvider.getUserName();
			final String password = userSettingsProvider.getPassword();
			final EWorld world = userSettingsProvider.getWorld();
			final String emptyText = "";
			if (username == null || username.equals(emptyText) || password == null || password.equals(emptyText)
					|| world == null) {
				throw new EmptyUserCredentialsException();
			}

			// Create the store
			this.mStore = new Store(username, world);

			// Create Freewar API
			final EBrowser browser = browserSettingsProvider.getBrowser();
			this.mApi = new Sparkle(browser);

			// Set options
			final DesiredCapabilities capabilities = this.mApi.createCapabilities(browser,
					browserSettingsProvider.getDriverForBrowser(browser), browserSettingsProvider.getBrowserBinary(),
					browserSettingsProvider.getUserProfile());
			this.mApi.setCapabilities(capabilities);

			// Login and create an instance
			this.mInstance = this.mApi.login(username, password, world);
			this.mDriver = ((IHasWebDriver) this.mInstance).getWebDriver();

			// Create and start all services
			this.mDataBridge = new DataBridge(this.mDriver);
			this.mService = new Service(this.mApi, this.mInstance, this.mDriver, this.mStore, this);
			this.mPushDataService = new PushDataService(this.mService, this.mInstance, this.mDataBridge);
			this.mFetchDataService = new FetchDataService(this.mDataBridge);
			this.mService.registerFetchDataService(this.mFetchDataService);
			this.mService.registerPushDataService(this.mPushDataService);
			this.mService.start();
		} catch (final Exception e) {
			this.mLogger.logError("Error while starting service, shutting down: " + LoggerUtil.getStackTrace(e));
			// Try to shutdown and free all resources
			if (this.mStore != null) {
				this.mStore.shutdown();
			}
			if (this.mInstance != null && this.mApi != null) {
				this.mApi.logout(this.mInstance, false);
			}
			if (this.mApi != null) {
				this.mApi.shutdown(false);
			}

			shutdown();
		}
	}

	/**
	 * Stops the tool. In contrast to {@link #shutdown()} the tool object can be
	 * restarted with {@link #start()} after this method.
	 */
	public void stop() {
		try {
			stopLoginDialog();
		} catch (final Exception e) {
			this.mLogger.logError("Error while stopping login dialog: " + LoggerUtil.getStackTrace(e));
		}

		try {
			stopService();
		} catch (final Exception e) {
			this.mLogger.logError("Error while stopping service: " + LoggerUtil.getStackTrace(e));
		}
	}

	/**
	 * Stops the login dialog if present and active. The dialog can not be used
	 * anymore after calling this method. Instead restart the tool by calling
	 * {@link #stop()} and {@link #start()}.
	 */
	private void stopLoginDialog() {
		if (this.mLoginDialog != null && this.mLoginDialog.isActive()) {
			this.mLoginDialog.dispose();
			this.mLoginDialog = null;
		}
	}

	/**
	 * Stops the actual main service of the tool if present and active. The
	 * service can not be used anymore after calling this method. Instead
	 * restart the tool by calling {@link #stop()} and {@link #start()}.
	 */
	private void stopService() {
		if (this.mService != null && this.mService.isActive()) {
			try {
				this.mService.stopService();
			} catch (final Exception e) {
				this.mLogger.logError("Error while stopping service: " + LoggerUtil.getStackTrace(e));
			}

			try {
				this.mStore.shutdown();
				this.mLogger.flush();
			} catch (final Exception e) {
				this.mLogger.logError("Error while finalizing store: " + LoggerUtil.getStackTrace(e));
			}
		}
	}

}
