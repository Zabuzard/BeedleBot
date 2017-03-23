package de.zabuza.beedlebot.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.tray.listener.ExitListener;
import de.zabuza.beedlebot.tray.listener.RestartListener;

/**
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class TrayManager {
	private final BeedleBot mBeedleBot;
	private SystemTray mSystemTray;
	private TrayIcon mTrayIcon;
	
	public TrayManager(final BeedleBot beedleBot) throws IOException {
		//TODO Correct error handling, don't use throw
		mBeedleBot = beedleBot;
		initialize();
	}
	
	private void initialize() throws IOException {
		// If try is not supported, abort
		if (!SystemTray.isSupported()) {
			//TODO Logging
			return;
		}
		
		mSystemTray = SystemTray.getSystemTray();
		
		//TODO Correct error handling, don't use throw
		//TODO Use constants for text
		final Image trayIconImage = ImageIO.read(new File("res/img/trayIcon.png"));
		mTrayIcon = new TrayIcon(trayIconImage, "BeedleBot");
		mTrayIcon.setImageAutoSize(true);
		
		final MenuItem restartItem = new MenuItem("Restart");
		final MenuItem exitItem = new MenuItem("Exit");
		
		final PopupMenu popup = new PopupMenu();
		popup.add(restartItem);
		popup.add(exitItem);
		mTrayIcon.setPopupMenu(popup);
		
		restartItem.addActionListener(new RestartListener(mBeedleBot));
		exitItem.addActionListener(new ExitListener(mBeedleBot));
	}
	
	public void addTrayIcon() throws AWTException {
		//TODO Correct error handling, don't use throw
		mSystemTray.add(mTrayIcon);
	}
	
	public void removeTrayIcon() throws AWTException {
		//TODO Correct error handling, don't use throw
		mSystemTray.remove(mTrayIcon);
	}
}
