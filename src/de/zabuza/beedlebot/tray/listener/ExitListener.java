package de.zabuza.beedlebot.tray.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.zabuza.beedlebot.BeedleBot;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;

/**
 * Listener to use for exiting the tool. When the event arrives it performs
 * {@link BeedleBot#shutdown()}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ExitListener implements ActionListener {
	/**
	 * The parent tool to shutdown when the event arrives.
	 */
	private final BeedleBot mBeedleBot;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;

	/**
	 * Creates a new exit listener that shutdowns the given tool when an action
	 * event arrives. Therefore it performs {@link BeedleBot#shutdown()}.
	 * 
	 * @param beedleBot
	 *            The tool to shutdown when the event arrives
	 */
	public ExitListener(final BeedleBot beedleBot) {
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
		this.mLogger.logInfo("Executing exit action");
		this.mBeedleBot.shutdown();
	}

}
