package de.zabuza.beedlebot.exceptions;

import de.zabuza.sparkle.freewar.EWorld;

public final class PlayerPriceServiceAnswerWrongFormatException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public PlayerPriceServiceAnswerWrongFormatException(final String itemName, final EWorld world) {
		super(itemName + ", " + world.toString());
	}

}
