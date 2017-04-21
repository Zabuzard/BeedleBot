package de.zabuza.beedlebot.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.exceptions.UnsupportedSystemTrayException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
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
	private final ILogger mLogger;
	private SystemTray mSystemTray;
	private TrayIcon mTrayIcon;
	private final Image mTrayIconImage;

	public TrayManager(final BeedleBot beedleBot, final Image trayIconImage) {
		this.mBeedleBot = beedleBot;
		this.mTrayIconImage = trayIconImage;
		this.mLogger = LoggerFactory.getLogger();
		initialize();
	}

	public void addTrayIcon() throws AWTException {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Adding tray icon");
		}
		this.mSystemTray.add(this.mTrayIcon);
	}

	public void removeTrayIcon() {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Removing tray icon");
		}
		this.mSystemTray.remove(this.mTrayIcon);
	}

	private void initialize() throws UnsupportedSystemTrayException {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Initializing TrayManager");
		}

		// If try is not supported, abort
		if (!SystemTray.isSupported()) {
			throw new UnsupportedSystemTrayException();
		}

		this.mSystemTray = SystemTray.getSystemTray();

		this.mTrayIcon = new TrayIcon(this.mTrayIconImage, NAME_TRAY);
		this.mTrayIcon.setImageAutoSize(true);

		final MenuItem restartItem = new MenuItem(NAME_RESTART);
		final MenuItem exitItem = new MenuItem(NAME_EXIT);

		final PopupMenu popup = new PopupMenu();
		popup.add(restartItem);
		popup.add(exitItem);
		this.mTrayIcon.setPopupMenu(popup);

		restartItem.addActionListener(new RestartListener(this.mBeedleBot));
		exitItem.addActionListener(new ExitListener(this.mBeedleBot));
	}
}
