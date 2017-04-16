package de.zabuza.beedlebot.exceptions;

import java.io.IOException;

public final class PurchaseRegisterServiceUnavailableException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public PurchaseRegisterServiceUnavailableException(final IOException cause) {
		super(cause);
	}

}
