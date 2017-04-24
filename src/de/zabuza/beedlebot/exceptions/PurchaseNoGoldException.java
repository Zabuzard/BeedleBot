package de.zabuza.beedlebot.exceptions;

/**
 * Exception that is thrown whenever trying to purchase an item though the
 * player has less gold than the cost of this item.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class PurchaseNoGoldException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

}
