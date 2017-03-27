package de.zabuza.beedlebot;

import java.awt.AWTException;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.databridge.io.FetchDataService;
import de.zabuza.beedlebot.databridge.io.PushDataService;
import de.zabuza.beedlebot.logindialog.LoginDialog;
import de.zabuza.beedlebot.logindialog.controller.settings.IBrowserSettingsProvider;
import de.zabuza.beedlebot.logindialog.controller.settings.IUserSettingsProvider;
import de.zabuza.beedlebot.service.BeedleService;
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
	private TrayManager mTrayManager;
	private LoginDialog mLoginDialog;
	private Image mIconImage;
	private IFreewarAPI mApi;
	private IFreewarInstance mInstance;
	private WebDriver mDriver;
	private DataBridge mDataBridge;
	private BeedleService mBeedleService;
	private PushDataService mPushDataService;
	private FetchDataService mFetchDataService;

	public BeedleBot() {
		mTrayManager = null;
		mLoginDialog = null;
		mIconImage = null;
		mApi = null;
		mInstance = null;
		mDriver = null;
		mDataBridge = null;
		mBeedleService = null;
		mPushDataService = null;
		mFetchDataService = null;
	}

	public void initialize() throws IOException, AWTException {
		// TODO Correct error handling, don't use throw
		mIconImage = ImageIO.read(new File("res/img/icon.png"));
		mTrayManager = new TrayManager(this, mIconImage);
		mTrayManager.addTrayIcon();
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

		// Create Freewar API
		final EBrowser browser = browserSettingsProvider.getBrowser();
		mApi = new Sparkle(browser);
		final Capabilities capabilities = mApi.createCapabilities(browser,
				browserSettingsProvider.getDriverForBrowser(browser), browserSettingsProvider.getBrowserBinary());
		mApi.setCapabilities(capabilities);

		// Login and create an instance
		final String username = userSettingsProvider.getUserName();
		final String password = userSettingsProvider.getPassword();
		final EWorld world = userSettingsProvider.getWorld();
		if (username == null || username.equals("") || password == null || password.equals("") || world == null) {
			// TODO Correct error handling and logging
		}

		mInstance = mApi.login(username, password, world);
		mDriver = ((IHasWebDriver) mInstance).getWebDriver();

		// Create and start all services
		mDataBridge = new DataBridge(mDriver);
		mBeedleService = new BeedleService(mApi, mInstance);
		mPushDataService = new PushDataService(mBeedleService, mInstance, mDataBridge);
		mFetchDataService = new FetchDataService(mDataBridge);
		mBeedleService.registerFetchDataService(mFetchDataService);
		mBeedleService.registerPushDataService(mPushDataService);
		mBeedleService.start();
	}

	private void stopService() {
		if (mBeedleService != null && mBeedleService.isActive()) {
			mBeedleService.stopService();
		}
	}

	private void stopLoginDialog() {
		if (mLoginDialog != null && mLoginDialog.isActive()) {
			mLoginDialog.dispose();
			mLoginDialog = null;
		}
	}

	public void stop() {
		// TODO Remove debug
		System.out.println("Aus");

		stopLoginDialog();
		stopService();
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

}
