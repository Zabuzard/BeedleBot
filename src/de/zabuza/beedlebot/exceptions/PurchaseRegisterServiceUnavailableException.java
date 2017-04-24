package de.zabuza.beedlebot.exceptions;

import java.io.IOException;

/**
 * Exception that is thrown whenever the service used to register an item
 * purchase is unavailable such that a connection could not be established.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class PurchaseRegisterServiceUnavailableException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever the
	 * service used to register an item purchase is unavailable such that a
	 * connection could not be established.
	 * 
	 * @param cause
	 *            The exact cause that lead to this problem
	 */
	public PurchaseRegisterServiceUnavailableException(final IOException cause) {
		super(cause);
	}

}
