package de.zabuza.beedlebot.exceptions;

import de.zabuza.beedlebot.store.PlayerPrice;

/**
 * Exception that is thrown whenever there is no {@link PlayerPrice} for an item
 * though it was considered to be there.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class NoPlayerPriceThoughConsideredException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever there is
	 * no {@link PlayerPrice} for the given item though it was considered to be
	 * there.
	 * 
	 * @param itemName
	 *            The name of the item that has no player price
	 */
	public NoPlayerPriceThoughConsideredException(final String itemName) {
		super(itemName);
	}

}
