package de.zabuza.beedlebot.exceptions;

import java.awt.Point;

public final class NotAtCentralTradersDepotException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public NotAtCentralTradersDepotException(final Point currentLocation) {
		super(currentLocation.toString());
	}

}
