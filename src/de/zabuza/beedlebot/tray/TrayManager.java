package de.zabuza.beedlebot.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.exceptions.UnsupportedSystemTrayException;
import de.zabuza.beedlebot.tray.listener.ExitListener;
import de.zabuza.beedlebot.tray.listener.RestartListener;

/**
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class TrayManager {
	private static final String NAME_EXIT = "Exit";
	private static final String NAME_RESTART = "Restart";
	private static final String NAME_TRAY = "Beedlebot";
	private final BeedleBot mBeedleBot;
	private SystemTray mSystemTray;
	private TrayIcon mTrayIcon;
	private final Image mTrayIconImage;

	public TrayManager(final BeedleBot beedleBot, final Image trayIconImage) {
		mBeedleBot = beedleBot;
		mTrayIconImage = trayIconImage;
		initialize();
	}

	public void addTrayIcon() throws AWTException {
		mSystemTray.add(mTrayIcon);
	}

	public void removeTrayIcon() {
		mSystemTray.remove(mTrayIcon);
	}

	private void initialize() throws UnsupportedSystemTrayException {
		// If try is not supported, abort
		if (!SystemTray.isSupported()) {
			throw new UnsupportedSystemTrayException();
		}

		mSystemTray = SystemTray.getSystemTray();

		mTrayIcon = new TrayIcon(mTrayIconImage, NAME_TRAY);
		mTrayIcon.setImageAutoSize(true);

		final MenuItem restartItem = new MenuItem(NAME_RESTART);
		final MenuItem exitItem = new MenuItem(NAME_EXIT);

		final PopupMenu popup = new PopupMenu();
		popup.add(restartItem);
		popup.add(exitItem);
		mTrayIcon.setPopupMenu(popup);

		restartItem.addActionListener(new RestartListener(mBeedleBot));
		exitItem.addActionListener(new ExitListener(mBeedleBot));
	}
}
