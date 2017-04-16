package de.zabuza.beedlebot.exceptions;

import de.zabuza.sparkle.freewar.EWorld;

public final class StoreUnsupportedWorldException extends IllegalArgumentException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public StoreUnsupportedWorldException(final EWorld world) {
		super(world.toString());
	}

}
