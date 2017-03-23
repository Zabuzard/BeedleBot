package de.zabuza.beedlebot.logindialog.controller.settings;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JTextField;

import de.zabuza.beedlebot.logindialog.controller.listener.CloseAtCancelActionListener;
import de.zabuza.beedlebot.logindialog.controller.listener.ClosingCallbackWindowListener;
import de.zabuza.beedlebot.logindialog.controller.listener.FileChooseSetActionListener;
import de.zabuza.beedlebot.logindialog.controller.listener.SaveActionListener;
import de.zabuza.beedlebot.logindialog.controller.listener.SettingsActionListener;
import de.zabuza.beedlebot.logindialog.view.LoginDialogView;
import de.zabuza.beedlebot.logindialog.view.SettingsDialog;
import de.zabuza.sparkle.freewar.EWorld;
import de.zabuza.sparkle.webdriver.EBrowser;

/**
 * The controller of the settings.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class SettingsController implements ISettingsProvider, IBrowserSettingsProvider, IUserSettingsProvider {
	/**
	 * Text to save for a value if a key is unknown.
	 */
	public static final String UNKNOWN_KEY_VALUE = "";
	/**
	 * Key identifier for binary setting.
	 */
	private static final String KEY_IDENTIFIER_BINARY = "binary";
	/**
	 * Key identifier for the selected browser.
	 */
	private static final String KEY_IDENTIFIER_BROWSER = "browser";
	/**
	 * Key identifier for driver settings.
	 */
	private static final String KEY_IDENTIFIER_DRIVER = "driver";
	/**
	 * Key identifier for the password.
	 */
	private static final String KEY_IDENTIFIER_PASSWORD = "password";
	/**
	 * Key identifier for the username.
	 */
	private static final String KEY_IDENTIFIER_USERNAME = "username";
	/**
	 * Key identifier for the selected world.
	 */
	private static final String KEY_IDENTIFIER_WORLD = "world";
	/**
	 * Separator which separates several information in a key.
	 */
	private static final String KEY_INFO_SEPARATOR = "@";
	/**
	 * The owning frame of this controller.
	 */
	private final JFrame mOwner;
	/**
	 * The object for the settings.
	 */
	private final Settings mSettings;
	/**
	 * The settings dialog or <tt>null</tt> if currently not opened.
	 */
	private SettingsDialog mSettingsDialog;
	/**
	 * Structure which saves all currently loaded settings.
	 */
	private final Map<String, String> mSettingsStore;
	/**
	 * The view of the login dialog.
	 */
	private final LoginDialogView mView;

	/**
	 * Creates a new controller of the settings.
	 * 
	 * @param owner
	 *            The owning frame of this controller
	 * @param view
	 *            The view to control
	 */
	public SettingsController(final JFrame owner, final LoginDialogView view) {
		mView = view;
		mOwner = owner;

		mSettingsStore = new HashMap<>();
		mSettings = new Settings();
		mSettingsDialog = null;
	}

	/**
	 * Call whenever the settings dialog is closing. This is used as callback to
	 * free the parent window of the dialog.
	 */
	public void closingSettingsDialog() {
		mView.setAllInputEnabled(true);
		mView.setLoginButtonEnabled(true);
		mView.setSettingsButtonEnabled(true);
		mSettingsDialog = null;
	}

	/**
	 * Call whenever the save action is to be executed. This will save all
	 * settings and close the settings dialog, if opened.
	 */
	public void executeSaveAction() {
		// Save dialog settings if dialog is opened
		if (mSettingsDialog != null) {
			// Driver settings
			for (final EBrowser browser : EBrowser.values()) {
				final JTextField field = mSettingsDialog.getBrowserDriverField(browser);
				final String value = field.getText();
				if (!value.equals(UNKNOWN_KEY_VALUE)) {
					final String key = KEY_IDENTIFIER_DRIVER + KEY_INFO_SEPARATOR + browser;
					setSetting(key, value);
				}
			}

			// Binary setting
			final JTextField binaryField = mSettingsDialog.getBrowserBinaryField();
			final String binaryValue = binaryField.getText();
			if (!binaryValue.equals(UNKNOWN_KEY_VALUE)) {
				final String key = KEY_IDENTIFIER_BINARY;
				setSetting(key, binaryValue);
			}
		}

		// Save the current content of the main view
		// Username
		final String username = mView.getUsername();
		if (!username.equals(UNKNOWN_KEY_VALUE)) {
			final String key = KEY_IDENTIFIER_USERNAME;
			setSetting(key, username);
		}

		// Password
		final String password = mView.getPassword();
		if (!password.equals(UNKNOWN_KEY_VALUE)) {
			final String key = KEY_IDENTIFIER_PASSWORD;
			setSetting(key, password);
		}

		// World
		final EWorld world = mView.getWorld();
		if (world != null) {
			final String key = KEY_IDENTIFIER_WORLD;
			setSetting(key, world.toString());
		}

		// Selected browser
		final EBrowser browser = mView.getBrowser();
		if (browser != null) {
			final String key = KEY_IDENTIFIER_BROWSER;
			setSetting(key, browser.toString());
		}

		// Save settings
		mSettings.saveSettings(this);

		// Close the settings dialog, if opened
		if (mSettingsDialog != null) {
			mSettingsDialog.dispatchEvent(new WindowEvent(mSettingsDialog, WindowEvent.WINDOW_CLOSING));
		}
	}

	/**
	 * Call whenever the settings action is to be executed. This will open a
	 * settings dialog.
	 */
	public void executeSettingsAction() {
		// Deactivate all actions until the settings dialog has closed
		mView.setAllInputEnabled(false);
		mView.setLoginButtonEnabled(false);
		mView.setSettingsButtonEnabled(false);

		// Open the dialog
		mSettingsDialog = new SettingsDialog(mOwner);
		linkDialogListener();

		// Load settings to the store
		passSettingsToSettingsDialogView();
		mSettingsDialog.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.kivabot.controller.settings.ISettingsProvider#getAllSettings()
	 */
	@Override
	public Map<String, String> getAllSettings() {
		return mSettingsStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.kivabot.model.IBrowserSettingsProvider#getBrowserBinary()
	 */
	@Override
	public String getBrowserBinary() {
		String binary = getSetting(KEY_IDENTIFIER_BINARY);
		if (binary.equals(UNKNOWN_KEY_VALUE)) {
			return null;
		} else {
			return binary;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.kivabot.model.IBrowserSettingsProvider#getDriverForBrowser(de.
	 * zabuza.sparkle.webdriver.EBrowser)
	 */
	@Override
	public String getDriverForBrowser(final EBrowser browser) {
		String key = KEY_IDENTIFIER_DRIVER + KEY_INFO_SEPARATOR + browser;
		String driver = getSetting(key);
		if (driver.equals(UNKNOWN_KEY_VALUE)) {
			return null;
		} else {
			return driver;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.kivabot.controller.settings.ISettingsProvider#getSetting(java.
	 * lang.String)
	 */
	@Override
	public String getSetting(final String key) {
		String value = mSettingsStore.get(key);
		if (value == null) {
			value = UNKNOWN_KEY_VALUE;
		}
		return value;
	}

	/**
	 * Initializes the controller.
	 */
	public void initialize() {
		linkListener();
		mSettings.loadSettings(this);
	}

	/**
	 * Passes the settings of the store to the main view for display.
	 */
	public void passSettingsToMainView() {
		for (final Entry<String, String> entry : mSettingsStore.entrySet()) {
			final String[] keySplit = entry.getKey().split(KEY_INFO_SEPARATOR);
			final String keyIdentifier = keySplit[0];

			if (keyIdentifier.equals(KEY_IDENTIFIER_USERNAME)) {
				// Username
				mView.setUsername(entry.getValue());
			} else if (keyIdentifier.equals(KEY_IDENTIFIER_PASSWORD)) {
				// Password
				mView.setPassword(entry.getValue());
			} else if (keyIdentifier.equals(KEY_IDENTIFIER_WORLD)) {
				// World
				mView.setWorld(EWorld.valueOf(entry.getValue()));
			} else if (keyIdentifier.equals(KEY_IDENTIFIER_BROWSER)) {
				// Browser
				mView.setBrowser(EBrowser.valueOf(entry.getValue()));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.kivabot.controller.settings.ISettingsProvider#setSetting(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public void setSetting(final String key, final String value) {
		mSettingsStore.put(key, value);
	}

	/**
	 * Links the listener of the dialog to it.
	 */
	private void linkDialogListener() {
		// Window listener
		mSettingsDialog.addWindowListener(new ClosingCallbackWindowListener(this));

		// Browser field listener
		for (EBrowser browser : EBrowser.values()) {
			ActionListener listener = new FileChooseSetActionListener(mSettingsDialog,
					mSettingsDialog.getBrowserDriverField(browser));
			mSettingsDialog.addListenerToBrowserDriverSelectionAction(browser, listener);
		}

		// Binary listener
		ActionListener listener = new FileChooseSetActionListener(mSettingsDialog,
				mSettingsDialog.getBrowserBinaryField());
		mSettingsDialog.addListenerToBrowserBinarySelectionAction(listener);

		// Save and cancel listener
		mSettingsDialog.addListenerToSaveAction(new SaveActionListener(this));
		mSettingsDialog.addListenerToCancelAction(new CloseAtCancelActionListener(mSettingsDialog));
	}

	/**
	 * Links the listener to the view.
	 */
	private void linkListener() {
		mView.addListenerToSettingsAction(new SettingsActionListener(this));
	}

	/**
	 * Passes the settings of the store to the settings dialog view for display.
	 */
	private void passSettingsToSettingsDialogView() {
		for (final Entry<String, String> entry : mSettingsStore.entrySet()) {
			final String[] keySplit = entry.getKey().split(KEY_INFO_SEPARATOR);
			final String keyIdentifier = keySplit[0];

			if (keyIdentifier.equals(KEY_IDENTIFIER_DRIVER)) {
				// Driver settings
				final EBrowser browser = EBrowser.valueOf(keySplit[1]);
				final JTextField field = mSettingsDialog.getBrowserDriverField(browser);
				field.setText(entry.getValue());
			} else if (keyIdentifier.equals(KEY_IDENTIFIER_BINARY)) {
				// Binary settings
				final JTextField field = mSettingsDialog.getBrowserBinaryField();
				field.setText(entry.getValue());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.beedlebot.logindialog.controller.settings.IUserSettingsProvider
	 * #getUserName()
	 */
	@Override
	public String getUserName() {
		return getSetting(KEY_IDENTIFIER_USERNAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.beedlebot.logindialog.controller.settings.IUserSettingsProvider
	 * #getPassword()
	 */
	@Override
	public String getPassword() {
		return getSetting(KEY_IDENTIFIER_PASSWORD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.beedlebot.logindialog.controller.settings.IUserSettingsProvider
	 * #getWorld()
	 */
	@Override
	public EWorld getWorld() {
		String value = getSetting(KEY_IDENTIFIER_WORLD);
		if (value != null) {
			return EWorld.valueOf(value);
		} else {
			return null;
		}
	}
}
