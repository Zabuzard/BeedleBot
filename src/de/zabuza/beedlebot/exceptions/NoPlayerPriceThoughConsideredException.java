package de.zabuza.beedlebot.exceptions;

public final class NoPlayerPriceThoughConsideredException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public NoPlayerPriceThoughConsideredException(final String itemName) {
		super(itemName);
	}

}
