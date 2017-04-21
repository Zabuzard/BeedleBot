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
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class BeedleBot {
	private static final String IMAGE_PATH_ICON = "res/img/icon.png";

	/**
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

	private IFreewarAPI mApi;
	private DataBridge mDataBridge;
	private WebDriver mDriver;
	private FetchDataService mFetchDataService;
	private Image mIconImage;
	private IFreewarInstance mInstance;
	private ILogger mLogger;
	private LoginDialog mLoginDialog;
	private PushDataService mPushDataService;
	private Service mService;

	private Store mStore;

	private TrayManager mTrayManager;

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

	public void initialize() throws IOException, AWTException {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Initializing BeedleBot");
		}
		this.mIconImage = ImageIO.read(new File(IMAGE_PATH_ICON));
		this.mTrayManager = new TrayManager(this, this.mIconImage);
		this.mTrayManager.addTrayIcon();
	}

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

	public void start() {
		this.mLogger.logInfo("BeedleBot start");

		this.mLoginDialog = new LoginDialog(this, this.mIconImage);
	}

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

	private void stopLoginDialog() {
		if (this.mLoginDialog != null && this.mLoginDialog.isActive()) {
			this.mLoginDialog.dispose();
			this.mLoginDialog = null;
		}
	}

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
