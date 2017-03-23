package de.zabuza.beedlebot.tray.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.zabuza.beedlebot.BeedleBot;

/**
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ExitListener implements ActionListener {
	private final BeedleBot mBeedleBot;

	public ExitListener(BeedleBot beedleBot) {
		mBeedleBot = beedleBot;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		mBeedleBot.shutdown();
	}

}
