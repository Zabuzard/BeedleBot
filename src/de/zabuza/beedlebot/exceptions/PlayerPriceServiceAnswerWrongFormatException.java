package de.zabuza.beedlebot.exceptions;

import de.zabuza.sparkle.freewar.EWorld;

/**
 * Exception that is thrown whenever the service used to fetch the player price
 * of an item sends an answer that is in the wrong format such that it could not
 * be parsed correctly.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class PlayerPriceServiceAnswerWrongFormatException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever the
	 * service used to fetch the player price of an item sends an answer that is
	 * in the wrong format such that it could not be parsed correctly.
	 * 
	 * @param itemName
	 *            The name of the item of the query that triggered the problem
	 * @param world
	 *            The world of the query that triggered the problem
	 */
	public PlayerPriceServiceAnswerWrongFormatException(final String itemName, final EWorld world) {
		super(itemName + ", " + world.toString());
	}

}
