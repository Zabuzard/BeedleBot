package de.zabuza.beedlebot.exceptions;

import java.io.UnsupportedEncodingException;

public final class UnexpectedUnsupportedEncodingException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public UnexpectedUnsupportedEncodingException(final UnsupportedEncodingException cause) {
		super(cause);
	}

}
