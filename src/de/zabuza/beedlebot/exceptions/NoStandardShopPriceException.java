package de.zabuza.beedlebot.exceptions;

/**
 * Exception that is thrown whenever an item has no standard shop price though
 * every item needs to have such a price.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class NoStandardShopPriceException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever the given
	 * item has no standard shop price though every item needs to have such a
	 * price.
	 * 
	 * @param itemName
	 *            The name of the item that has no standard shop price
	 */
	public NoStandardShopPriceException(final String itemName) {
		super(itemName);
	}

}
