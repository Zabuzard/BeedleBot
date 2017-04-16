package de.zabuza.beedlebot.exceptions;

public final class StoreCacheDeserializationUnsuccessfulException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public StoreCacheDeserializationUnsuccessfulException(final Exception cause) {
		super(cause);
	}

}
