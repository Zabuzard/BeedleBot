package de.zabuza.beedlebot.exceptions;

import java.io.IOException;

public final class LoggerCanNotReadException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public LoggerCanNotReadException(final IOException cause) {
		super(cause);
	}

}
