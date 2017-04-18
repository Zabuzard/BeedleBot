package de.zabuza.beedlebot.logindialog;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.logging.LoggerUtil;
import de.zabuza.beedlebot.logindialog.controller.LoginDialogController;
import de.zabuza.beedlebot.logindialog.view.LoginDialogView;

/**
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class LoginDialog {

	private static final String FRAME_TITLE = "BeedleBot Login";
	private JFrame mFrame;
	private ILogger mLogger;

	public LoginDialog(final BeedleBot beedleBot, final Image iconImage) {
		mLogger = LoggerFactory.getLogger();

		EventQueue.invokeLater(new Runnable() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				mLogger.logInfo("Starting LoginDialog");
				mFrame = null;
				LoginDialogView window = null;
				try {
					mFrame = new JFrame();
					mFrame.setResizable(false);
					mFrame.setTitle(FRAME_TITLE);
					mFrame.setIconImage(iconImage);
					mFrame.setBounds(0, 0, LoginDialogView.WIDTH, LoginDialogView.HEIGHT);
					mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					mFrame.getContentPane().setLayout(null);
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					mFrame.setLocation((screenSize.width - mFrame.getWidth()) / 2,
							(screenSize.height - mFrame.getHeight()) / 2);

					window = new LoginDialogView(mFrame);
					LoginDialogController controller = new LoginDialogController(mFrame, window, beedleBot);
					controller.initialize();
					controller.start();
				} catch (final Exception e) {
					mLogger.logError(
							"Error while starting login service, shutting down: " + LoggerUtil.getStackTrace(e));
					// Try to shutdown
					dispose();
					beedleBot.shutdown();
				} finally {
					if (mFrame != null) {
						mFrame.setVisible(true);
					}
				}
			}
		});
	}

	public void dispose() {
		if (mLogger.isDebugEnabled()) {
			mLogger.logDebug("Disposing LoginDialog");
		}

		if (mFrame != null) {
			mFrame.setVisible(false);
			mFrame.dispose();
			mFrame = null;
		}
	}

	public boolean isActive() {
		return mFrame != null && mFrame.isVisible();
	}
}
