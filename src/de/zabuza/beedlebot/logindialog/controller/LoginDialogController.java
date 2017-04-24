package de.zabuza.beedlebot.logindialog.controller;

import javax.swing.JFrame;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.logging.LoggerUtil;
import de.zabuza.beedlebot.logindialog.controller.listener.ExitAtWindowCloseListener;
import de.zabuza.beedlebot.logindialog.controller.listener.LoginActionListener;
import de.zabuza.beedlebot.logindialog.controller.settings.SettingsController;
import de.zabuza.beedlebot.logindialog.view.LoginDialogView;

/**
 * The controller of the login dialog.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 * 
 */
public final class LoginDialogController {
	/**
	 * The tool to use for callbacks regarding starting the service.
	 */
	private final BeedleBot mBeedleBot;
	/**
	 * The logger used for logging.
	 */
	private final ILogger mLogger;
	/**
	 * The frame that holds the dialog.
	 */
	private final JFrame mOwner;
	/**
	 * The controller for the settings.
	 */
	private final SettingsController mSettingsController;
	/**
	 * The view of the login dialog.
	 */
	private final LoginDialogView mView;

	/**
	 * Creates a new controller of the login dialog by connecting it to the
	 * view. After creation use {@link #initialize()} and then {@link #start()}.
	 * 
	 * @param owner
	 *            The owning frame of this controller
	 * @param view
	 *            View of the main frame
	 * @param beedleBot
	 *            The tool to use for callbacks regarding starting the service
	 */
	public LoginDialogController(final JFrame owner, final LoginDialogView view, final BeedleBot beedleBot) {
		this.mOwner = owner;
		this.mView = view;
		this.mBeedleBot = beedleBot;
		this.mSettingsController = new SettingsController(owner, view);
		this.mLogger = LoggerFactory.getLogger();
	}

	/**
	 * Initializes the controller. Use this method prior to {@link #start()}.
	 */
	public void initialize() {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Initializing LoginDialogController");
		}

		linkListener();
		this.mSettingsController.initialize();
		// Pass the saved settings to the view
		this.mSettingsController.passSettingsToMainView();
	}

	/**
	 * Shuts the whole tool down.
	 */
	public void shutdownTool() {
		this.mBeedleBot.shutdown();
	}

	/**
	 * Starts the controller. Use {@link #initialize()} prior to this method.
	 */
	public void start() {
		// Nothing to do there yet
	}

	/**
	 * Starts the login to the service and disposes the login dialog.
	 */
	public void startLogin() {
		try {
			// First save the current content of the view
			this.mSettingsController.executeSaveAction();

			// Close the dialog
			this.mOwner.setVisible(false);
			this.mOwner.dispose();

			// Start the login
			this.mBeedleBot.startService(this.mSettingsController, this.mSettingsController);
		} catch (final Exception e) {
			this.mLogger.logError("Error while starting login, shutting down: " + LoggerUtil.getStackTrace(e));
			// Try to shutdown
			this.mBeedleBot.shutdown();
		}
	}

	/**
	 * Links the listener to the view.
	 */
	private void linkListener() {
		this.mView.addListenerToLoginAction(new LoginActionListener(this));
		this.mView.addWindowListener(new ExitAtWindowCloseListener(this));

		this.mOwner.getRootPane().setDefaultButton(this.mView.getLoginButton());
	}
}