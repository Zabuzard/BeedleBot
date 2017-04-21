package de.zabuza.beedlebot.logindialog;

import java.awt.EventQueue;
import java.awt.Image;
import javax.swing.JFrame;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;

/**
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class LoginDialog {

	private JFrame mFrame;
	private final ILogger mLogger;
	private final LoginDialogRunnable mLoginDialogRunnable;

	public LoginDialog(final BeedleBot beedleBot, final Image iconImage) {
		this.mLogger = LoggerFactory.getLogger();
		this.mLoginDialogRunnable = new LoginDialogRunnable(beedleBot, this.mFrame, iconImage, this.mLogger);

		EventQueue.invokeLater(this.mLoginDialogRunnable);
	}

	public void dispose() {
		this.mLoginDialogRunnable.dispose();
	}

	public boolean isActive() {
		return this.mLoginDialogRunnable.isActive();
	}
}
