package de.zabuza.beedlebot.logindialog;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.logindialog.controller.LoginDialogController;
import de.zabuza.beedlebot.logindialog.view.LoginDialogView;

/**
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class LoginDialog {
	
	private JFrame mFrame;
	
	public LoginDialog(final BeedleBot beedleBot, final Image iconImage) {
		EventQueue.invokeLater(new Runnable() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				mFrame = null;
				LoginDialogView window = null;
				try {
					mFrame = new JFrame();
					mFrame.setResizable(false);
					// TODO Use constants for text
					mFrame.setTitle("BeedleBot Login");
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
				} catch (Exception e) {
					// TODO Correct error handling
				} finally {
					if (mFrame != null) {
						mFrame.setVisible(true);
					}
				}
			}
		});
	}
	
	public boolean isActive() {
		return mFrame != null && mFrame.isVisible();
	}
	
	public void dispose() {
		if (mFrame != null) {
			mFrame.setVisible(false);
			mFrame.dispose();
		}
	}
}
