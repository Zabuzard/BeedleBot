package de.zabuza.beedlebot.exceptions;

import de.zabuza.sparkle.webdriver.EBrowser;

public final class UnsupportedBrowserException extends IllegalArgumentException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedBrowserException(final EBrowser browser) {
		super(browser.toString());
	}

}
