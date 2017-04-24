package de.zabuza.beedlebot.exceptions;

import java.io.IOException;

import de.zabuza.beedlebot.store.StoreCache;

/**
 * Exception that is thrown whenever the serialization of a {@link StoreCache}
 * was unsuccessful.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class StoreCacheSerializationUnsuccessfulException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever the
	 * serialization of a {@link StoreCache} was unsuccessful.
	 * 
	 * @param cause
	 *            The exact cause that lead to this problem
	 */
	public StoreCacheSerializationUnsuccessfulException(final IOException cause) {
		super(cause);
	}

}
