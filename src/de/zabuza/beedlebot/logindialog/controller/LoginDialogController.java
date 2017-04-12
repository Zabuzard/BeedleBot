package de.zabuza.beedlebot.logindialog.controller;

import javax.swing.JFrame;

import de.zabuza.beedlebot.BeedleBot;
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
	 * The beedle bot tool.
	 */
	private final BeedleBot mBeedleBot;
	/**
	 * The owning frame of this controller.
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
	 * view.
	 * 
	 * @param owner
	 *            The owning frame of this controller
	 * @param view
	 *            View of the main frame
	 * @param beedleBot
	 *            The beedle bot tool
	 */
	public LoginDialogController(final JFrame owner, final LoginDialogView view, final BeedleBot beedleBot) {
		mOwner = owner;
		mView = view;
		mBeedleBot = beedleBot;
		mSettingsController = new SettingsController(owner, view);
	}

	/**
	 * Initializes the controller.
	 */
	public void initialize() {
		linkListener();
		mSettingsController.initialize();
		// Pass the saved settings to the view
		mSettingsController.passSettingsToMainView();
	}

	/**
	 * Shuts the beedle bot tool down.
	 */
	public void shutdownTool() {
		mBeedleBot.shutdown();
	}

	/**
	 * Starts the controller.
	 */
	public void start() {

	}

	/**
	 * Starts the login.
	 */
	public void startLogin() {
		// First save the current content of the view
		mSettingsController.executeSaveAction();

		// Close the dialog
		mOwner.setVisible(false);
		mOwner.dispose();

		// Start the login
		mBeedleBot.startService(mSettingsController, mSettingsController);
	}

	/**
	 * Links the listener to the view.
	 */
	private void linkListener() {
		mView.addListenerToLoginAction(new LoginActionListener(this));
		mView.addWindowListener(new ExitAtWindowCloseListener(this));

		mOwner.getRootPane().setDefaultButton(mView.getLoginButton());
	}
}