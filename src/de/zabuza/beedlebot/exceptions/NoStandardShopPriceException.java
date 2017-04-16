package de.zabuza.beedlebot.exceptions;

public final class NoStandardShopPriceException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public NoStandardShopPriceException(final String itemName) {
		super(itemName);
	}

}
