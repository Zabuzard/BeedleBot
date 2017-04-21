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

public final class LoginDialogRunnable implements Runnable {

	private static final String FRAME_TITLE = "BeedleBot Login";
	private final BeedleBot mBeedleBot;
	private JFrame mFrame;
	private final Image mIconImage;
	private final ILogger mLogger;

	public LoginDialogRunnable(final BeedleBot beedleBot, final JFrame frame, final Image iconImage,
			final ILogger logger) {
		this.mFrame = frame;
		this.mLogger = logger;
		this.mIconImage = iconImage;
		this.mBeedleBot = beedleBot;
	}

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
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.mFrame.setLocation((screenSize.width - this.mFrame.getWidth()) / 2,
					(screenSize.height - this.mFrame.getHeight()) / 2);

			window = new LoginDialogView(this.mFrame);
			LoginDialogController controller = new LoginDialogController(this.mFrame, window, this.mBeedleBot);
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
