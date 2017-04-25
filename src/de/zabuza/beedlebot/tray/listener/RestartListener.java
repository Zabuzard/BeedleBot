package de.zabuza.beedlebot.tray.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;

/**
 * Listener to use for restarting the tool. When the event arrives it performs
 * {@link BeedleBot#stop()} and {@link BeedleBot#start()}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class RestartListener implements ActionListener {
	/**
	 * The parent tool to restart when the event arrives.
	 */
	private final BeedleBot mBeedleBot;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;

	/**
	 * Creates a new restart listener that restarts the given tool when an
	 * action event arrives. Therefore it performs {@link BeedleBot#stop()} and
	 * {@link BeedleBot#start()}.
	 * 
	 * @param beedleBot
	 *            The tool to shutdown when the event arrives
	 */
	public RestartListener(final BeedleBot beedleBot) {
		this.mBeedleBot = beedleBot;
		this.mLogger = LoggerFactory.getLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		this.mLogger.logInfo("Executing restart action");
		this.mBeedleBot.stop();
		this.mBeedleBot.start();
	}

}
