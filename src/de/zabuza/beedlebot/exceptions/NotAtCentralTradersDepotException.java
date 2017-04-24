package de.zabuza.beedlebot.exceptions;

import java.awt.Point;

/**
 * Exception that is thrown whenever the tool is started but the players current
 * position is not at the central traders depot.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class NotAtCentralTradersDepotException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever the tool
	 * is started but the players current position is not at the central traders
	 * depot.
	 * 
	 * @param currentLocation
	 *            The current location of the player
	 */
	public NotAtCentralTradersDepotException(final Point currentLocation) {
		super(currentLocation.toString());
	}

}
