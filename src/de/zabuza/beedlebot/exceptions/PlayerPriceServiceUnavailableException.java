package de.zabuza.beedlebot.exceptions;

import java.io.IOException;

public final class PlayerPriceServiceUnavailableException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public PlayerPriceServiceUnavailableException(final IOException cause) {
		super(cause);
	}

}
