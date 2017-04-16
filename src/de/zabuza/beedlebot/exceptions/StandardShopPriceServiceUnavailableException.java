package de.zabuza.beedlebot.exceptions;

import java.io.IOException;

public final class StandardShopPriceServiceUnavailableException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public StandardShopPriceServiceUnavailableException(final IOException cause) {
		super(cause);
	}

}
