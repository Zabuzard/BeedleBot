package de.zabuza.beedlebot.exceptions;

public final class PageContentWrongFormatException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public PageContentWrongFormatException(final String pageContent) {
		super(pageContent);
	}

}
