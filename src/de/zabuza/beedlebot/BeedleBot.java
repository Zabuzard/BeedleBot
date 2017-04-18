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
		mTrayManager = null;
		mLoginDialog = null;
		mIconImage = null;
		mApi = null;
		mInstance = null;
		mDriver = null;
		mDataBridge = null;
		mService = null;
		mPushDataService = null;
		mFetchDataService = null;
		mStore = null;
		mLogger = LoggerFactory.getLogger();
	}

	public void initialize() throws IOException, AWTException {
		if (mLogger.isDebugEnabled()) {
			mLogger.logDebug("Initializing BeedleBot");
		}
		mIconImage = ImageIO.read(new File(IMAGE_PATH_ICON));
		mTrayManager = new TrayManager(this, mIconImage);
		mTrayManager.addTrayIcon();
	}

	public void shutdown() {
		mLogger.flush();
		if (mLogger.isDebugEnabled()) {
			mLogger.logDebug("Shutting down BeedleBot");
		}
		try {
			stop();
		} catch (final Exception e) {
			mLogger.logError("Error while stopping: " + LoggerUtil.getStackTrace(e));
		}

		if (mTrayManager != null) {
			try {
				mTrayManager.removeTrayIcon();
			} catch (final Exception e) {
				mLogger.logError("Error while removing tray icon: " + LoggerUtil.getStackTrace(e));
			}

		}

		mLogger.logInfo("BeedleBot shutdown");
		mLogger.close();
	}

	public void start() {
		mLogger.logInfo("BeedleBot start");

		mLoginDialog = new LoginDialog(this, mIconImage);
	}

	public void startService(final IUserSettingsProvider userSettingsProvider,
			final IBrowserSettingsProvider browserSettingsProvider) throws EmptyUserCredentialsException {
		try {
			mLogger.logInfo("Starting service");

			final String username = userSettingsProvider.getUserName();
			final String password = userSettingsProvider.getPassword();
			final EWorld world = userSettingsProvider.getWorld();
			final String emptyText = "";
			if (username == null || username.equals(emptyText) || password == null || password.equals(emptyText)
					|| world == null) {
				throw new EmptyUserCredentialsException();
			}

			// Create the store
			mStore = new Store(username, world);

			// Create Freewar API
			final EBrowser browser = browserSettingsProvider.getBrowser();
			mApi = new Sparkle(browser);

			// Set options
			final DesiredCapabilities capabilities = mApi.createCapabilities(browser,
					browserSettingsProvider.getDriverForBrowser(browser), browserSettingsProvider.getBrowserBinary(),
					browserSettingsProvider.getUserProfile());
			mApi.setCapabilities(capabilities);

			// Login and create an instance
			mInstance = mApi.login(username, password, world);
			mDriver = ((IHasWebDriver) mInstance).getWebDriver();

			// Create and start all services
			mDataBridge = new DataBridge(mDriver);
			mService = new Service(mApi, mInstance, mDriver, mStore, this);
			mPushDataService = new PushDataService(mService, mInstance, mDataBridge);
			mFetchDataService = new FetchDataService(mDataBridge);
			mService.registerFetchDataService(mFetchDataService);
			mService.registerPushDataService(mPushDataService);
			mService.start();
		} catch (final Exception e) {
			mLogger.logError("Error while starting service, shutting down: " + LoggerUtil.getStackTrace(e));
			// Try to shutdown and free all resources
			if (mStore != null) {
				mStore.finalize();
			}
			if (mInstance != null && mApi != null) {
				mApi.logout(mInstance, false);
			}
			if (mApi != null) {
				mApi.shutdown(false);
			}

			shutdown();
		}
	}

	public void stop() {
		try {
			stopLoginDialog();
		} catch (final Exception e) {
			mLogger.logError("Error while stopping login dialog: " + LoggerUtil.getStackTrace(e));
		}

		try {
			stopService();
		} catch (final Exception e) {
			mLogger.logError("Error while stopping service: " + LoggerUtil.getStackTrace(e));
		}
	}

	private void stopLoginDialog() {
		if (mLoginDialog != null && mLoginDialog.isActive()) {
			mLoginDialog.dispose();
			mLoginDialog = null;
		}
	}

	private void stopService() {
		if (mService != null && mService.isActive()) {
			try {
				mService.stopService();
			} catch (final Exception e) {
				mLogger.logError("Error while stopping service: " + LoggerUtil.getStackTrace(e));
			}

			try {
				mStore.finalize();
				mLogger.flush();
			} catch (final Exception e) {
				mLogger.logError("Error while finalizing store: " + LoggerUtil.getStackTrace(e));
			}
		}
	}

}
