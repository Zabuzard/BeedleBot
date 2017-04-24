package de.zabuza.beedlebot.exceptions;

import de.zabuza.beedlebot.store.StoreCache;

/**
 * Exception that is thrown whenever the deserialization of a {@link StoreCache}
 * was unsuccessful.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class StoreCacheDeserializationUnsuccessfulException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever the
	 * deserialization of a {@link StoreCache} was unsuccessful.
	 * 
	 * @param cause
	 *            The exact cause that lead to this problem
	 */
	public StoreCacheDeserializationUnsuccessfulException(final Exception cause) {
		super(cause);
	}

}
