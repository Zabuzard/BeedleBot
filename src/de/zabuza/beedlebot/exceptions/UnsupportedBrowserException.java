package de.zabuza.beedlebot.exceptions;

import de.zabuza.sparkle.webdriver.EBrowser;

/**
 * Exception that is thrown whenever trying to operate with a {@link EBrowser}
 * that is not supported.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class UnsupportedBrowserException extends IllegalArgumentException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever trying to
	 * operate with a {@link EBrowser} that is not supported.
	 * 
	 * @param browser
	 *            The browser trying to operate on
	 */
	public UnsupportedBrowserException(final EBrowser browser) {
		super(browser.toString());
	}

}
