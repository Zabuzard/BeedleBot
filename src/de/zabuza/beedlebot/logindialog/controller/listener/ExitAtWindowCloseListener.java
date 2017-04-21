package de.zabuza.beedlebot.logindialog.controller.listener;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import de.zabuza.beedlebot.logindialog.controller.LoginDialogController;

/**
 * Exits the login dialog and shuts the tool down if window is closed.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ExitAtWindowCloseListener implements WindowListener {
	/**
	 * The controller of the login dialog.
	 */
	private final LoginDialogController mController;

	/**
	 * Creates a new window listener which exits the login dialog and shuts the
	 * tool down if the window is closed.
	 * 
	 * @param controller
	 *            Controller of the login dialog
	 */
	public ExitAtWindowCloseListener(final LoginDialogController controller) {
		this.mController = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(final WindowEvent event) {
		// Nothing to do there yet
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(final WindowEvent event) {
		// Nothing to do there yet
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(final WindowEvent event) {
		this.mController.shutdownTool();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.
	 * WindowEvent)
	 */
	@Override
	public void windowDeactivated(final WindowEvent event) {
		// Nothing to do there yet
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.
	 * WindowEvent)
	 */
	@Override
	public void windowDeiconified(final WindowEvent event) {
		// Nothing to do there yet
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(final WindowEvent event) {
		// Nothing to do there yet
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(final WindowEvent event) {
		// Nothing to do there yet
	}

}
