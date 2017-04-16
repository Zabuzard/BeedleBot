package de.zabuza.beedlebot.exceptions;

import java.io.IOException;

public final class StoreCacheSerializationUnsuccessfulException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public StoreCacheSerializationUnsuccessfulException(final IOException cause) {
		super(cause);
	}

}
