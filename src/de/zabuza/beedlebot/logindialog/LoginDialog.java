package de.zabuza.beedlebot.logindialog;

import java.awt.EventQueue;
import java.awt.Image;
import javax.swing.JFrame;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;

/**
 * Dialog that lets the user choose user, browser and various other settings and
 * starts the service. It automatically starts after creation. It can be
 * disposed using {@link #dispose()}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class LoginDialog {
	/**
	 * The frame that holds the dialog.
	 */
	private JFrame mFrame;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;
	/**
	 * The actual object that runs the dialog.
	 */
	private final LoginDialogRunnable mLoginDialogRunnable;

	/**
	 * Creates a new login dialog that automatically starts after creation. It
	 * can be disposed using {@link #dispose()}.
	 * 
	 * @param beedleBot
	 *            The tool to use for callbacks regarding starting the service
	 * @param iconImage
	 *            The image to use as icon of the dialog
	 */
	public LoginDialog(final BeedleBot beedleBot, final Image iconImage) {
		this.mLogger = LoggerFactory.getLogger();
		this.mLoginDialogRunnable = new LoginDialogRunnable(beedleBot, this.mFrame, iconImage, this.mLogger);

		EventQueue.invokeLater(this.mLoginDialogRunnable);
	}

	/**
	 * Disposes and closes the dialog. Afterwards it can not be used anymore,
	 * instead create a new instance.
	 */
	public void dispose() {
		this.mLoginDialogRunnable.dispose();
	}

	/**
	 * Whether the login dialog is visible and active.
	 * 
	 * @return <tt>True</tt> if the dialog is visible and active, <tt>false</tt>
	 *         otherwise
	 */
	public boolean isActive() {
		return this.mLoginDialogRunnable.isActive();
	}
}
