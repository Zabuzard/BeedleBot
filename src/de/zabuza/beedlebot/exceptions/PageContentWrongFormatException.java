package de.zabuza.beedlebot.exceptions;

/**
 * Exception that is thrown whenever a raw page content is in the wrong format
 * and could not be parsed correctly.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class PageContentWrongFormatException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever a raw page
	 * content is in the wrong format and could not be parsed correctly.
	 * 
	 * @param pageContent
	 *            The raw page content that is in the wrong format
	 */
	public PageContentWrongFormatException(final String pageContent) {
		super(pageContent);
	}

}
