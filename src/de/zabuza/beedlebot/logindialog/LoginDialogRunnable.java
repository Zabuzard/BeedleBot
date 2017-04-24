package de.zabuza.beedlebot.logindialog;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerUtil;
import de.zabuza.beedlebot.logindialog.controller.LoginDialogController;
import de.zabuza.beedlebot.logindialog.view.LoginDialogView;

/**
 * Runnable that creates a login dialog which lets the user choose user, browser
 * and various other settings and starts the service. It can be disposed using
 * {@link #dispose()}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class LoginDialogRunnable implements Runnable {
	/**
	 * The title of the login dialog frame.
	 */
	private static final String FRAME_TITLE = "BeedleBot Login";
	/**
	 * The tool to use for callbacks regarding starting the service.
	 */
	private final BeedleBot mBeedleBot;
	/**
	 * The frame that holds the dialog.
	 */
	private JFrame mFrame;
	/**
	 * The image to use as icon of the dialog.
	 */
	private final Image mIconImage;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;

	/**
	 * Creates a new login dialog that can be started using {@link #run()} and
	 * stopped by {@link #dispose()}.
	 * 
	 * @param beedleBot
	 *            The tool to use for callbacks regarding starting the service
	 * @param frame
	 *            The frame that holds the dialog
	 * @param iconImage
	 *            The image to use as icon of the dialog
	 * @param logger
	 *            The logger to use for logging
	 */
	public LoginDialogRunnable(final BeedleBot beedleBot, final JFrame frame, final Image iconImage,
			final ILogger logger) {
		this.mFrame = frame;
		this.mLogger = logger;
		this.mIconImage = iconImage;
		this.mBeedleBot = beedleBot;
	}

	/**
	 * Disposes and closes the dialog. Afterwards it can not be used anymore,
	 * instead create a new instance.
	 */
	public void dispose() {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Disposing LoginDialog");
		}

		if (this.mFrame != null) {
			this.mFrame.setVisible(false);
			this.mFrame.dispose();
			this.mFrame = null;
		}
	}

	/**
	 * Whether the login dialog is visible and active.
	 * 
	 * @return <tt>True</tt> if the dialog is visible and active, <tt>false</tt>
	 *         otherwise
	 */
	public boolean isActive() {
		return this.mFrame != null && this.mFrame.isVisible();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.mLogger.logInfo("Starting LoginDialog");
		this.mFrame = null;
		LoginDialogView window = null;
		try {
			this.mFrame = new JFrame();
			this.mFrame.setResizable(false);
			this.mFrame.setTitle(FRAME_TITLE);
			this.mFrame.setIconImage(this.mIconImage);
			this.mFrame.setBounds(0, 0, LoginDialogView.WIDTH, LoginDialogView.HEIGHT);
			this.mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.mFrame.getContentPane().setLayout(null);
			final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.mFrame.setLocation((screenSize.width - this.mFrame.getWidth()) / 2,
					(screenSize.height - this.mFrame.getHeight()) / 2);

			window = new LoginDialogView(this.mFrame);
			final LoginDialogController controller = new LoginDialogController(this.mFrame, window, this.mBeedleBot);
			controller.initialize();
			controller.start();
		} catch (final Exception e) {
			this.mLogger.logError("Error while starting login service, shutting down: " + LoggerUtil.getStackTrace(e));
			// Try to shutdown
			dispose();
			this.mBeedleBot.shutdown();
		} finally {
			if (this.mFrame != null) {
				this.mFrame.setVisible(true);
			}
		}
	}

}
