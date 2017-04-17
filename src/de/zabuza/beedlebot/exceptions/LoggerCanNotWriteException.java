package de.zabuza.beedlebot.exceptions;

import java.io.IOException;

public final class LoggerCanNotWriteException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public LoggerCanNotWriteException(final IOException cause) {
		super(cause);
	}

}
