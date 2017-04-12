package de.zabuza.beedlebot;

import java.awt.AWTException;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.databridge.io.FetchDataService;
import de.zabuza.beedlebot.databridge.io.PushDataService;
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
	private static final String CHROME_USER_PROFILE_KEY = "user-data-dir";

	/**
	 * 
	 * @param args
	 *            Not supported
	 * @throws IOException
	 * @throws AWTException
	 */
	public static void main(final String[] args) throws IOException, AWTException {
		// TODO Correct error handling, don't use throw
		final BeedleBot beedleBot = new BeedleBot();
		beedleBot.initialize();
		beedleBot.start();
	}

	private IFreewarAPI mApi;
	private DataBridge mDataBridge;
	private WebDriver mDriver;
	private FetchDataService mFetchDataService;
	private Image mIconImage;
	private IFreewarInstance mInstance;
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
	}

	public void initialize() throws IOException, AWTException {
		// TODO Correct error handling, don't use throw
		mIconImage = ImageIO.read(new File("res/img/icon.png"));
		mTrayManager = new TrayManager(this, mIconImage);
		mTrayManager.addTrayIcon();
	}

	public void shutdown() {
		stop();
		try {
			// TODO Correct error handling
			mTrayManager.removeTrayIcon();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Shutdown");
	}

	public void start() {
		// TODO Remove debug
		System.out.println("An");

		mLoginDialog = new LoginDialog(this, mIconImage);
	}

	public void startService(final IUserSettingsProvider userSettingsProvider,
			final IBrowserSettingsProvider browserSettingsProvider) {
		// TODO Remove debug
		System.out.println("Starting Service");

		final String username = userSettingsProvider.getUserName();
		final String password = userSettingsProvider.getPassword();
		final EWorld world = userSettingsProvider.getWorld();
		if (username == null || username.equals("") || password == null || password.equals("") || world == null) {
			// TODO Correct error handling and logging
		}

		// Create the store
		mStore = new Store(username, world);

		// Create Freewar API
		final EBrowser browser = browserSettingsProvider.getBrowser();
		mApi = new Sparkle(browser);

		// Set options
		final DesiredCapabilities capabilities = mApi.createCapabilities(browser,
				browserSettingsProvider.getDriverForBrowser(browser), browserSettingsProvider.getBrowserBinary());
		final String userProfile = browserSettingsProvider.getUserProfile();
		// Try to set the profile if browser is chrome
		if (browser == EBrowser.CHROME) {
			// TODO Also support other browsers
			final ChromeOptions options = new ChromeOptions();
			options.addArguments(CHROME_USER_PROFILE_KEY + "=" + userProfile);
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		}
		mApi.setCapabilities(capabilities);

		// Login and create an instance
		mInstance = mApi.login(username, password, world);
		mDriver = ((IHasWebDriver) mInstance).getWebDriver();

		// Create and start all services
		mDataBridge = new DataBridge(mDriver);
		mService = new Service(mApi, mInstance, mDriver, mStore);
		mPushDataService = new PushDataService(mService, mInstance, mDataBridge);
		mFetchDataService = new FetchDataService(mDataBridge);
		mService.registerFetchDataService(mFetchDataService);
		mService.registerPushDataService(mPushDataService);
		mService.start();
	}

	public void stop() {
		// TODO Remove debug
		System.out.println("Aus");

		stopLoginDialog();
		stopService();
	}

	private void stopLoginDialog() {
		if (mLoginDialog != null && mLoginDialog.isActive()) {
			mLoginDialog.dispose();
			mLoginDialog = null;
		}
	}

	private void stopService() {
		if (mService != null && mService.isActive()) {
			mService.stopService();
			mStore.finalize();
		}
	}

}
