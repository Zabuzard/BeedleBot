package de.zabuza.beedlebot.logindialog.controller.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.zabuza.beedlebot.logindialog.controller.LoginDialogController;

/**
 * Listener of the login action.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public class LoginActionListener implements ActionListener {
	/**
	 * The controller of the main frame.
	 */
	private final LoginDialogController mController;

	/**
	 * Creates a new listener of the login action.
	 * 
	 * @param controller
	 *            Controller of the login dialog
	 */
	public LoginActionListener(final LoginDialogController controller) {
		mController = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		mController.startLogin();
	}
}
