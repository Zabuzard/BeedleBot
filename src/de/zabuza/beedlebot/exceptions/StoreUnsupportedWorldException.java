package de.zabuza.beedlebot.exceptions;

import de.zabuza.beedlebot.store.Store;
import de.zabuza.sparkle.freewar.EWorld;

/**
 * Exception that is thrown whenever trying to create an instance of a
 * {@link Store} that operates on a {@link EWorld} that is not supported by the
 * store.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class StoreUnsupportedWorldException extends IllegalArgumentException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever trying to
	 * create an instance of a {@link Store} that operates on a {@link EWorld}
	 * that is not supported by the store.
	 * 
	 * @param world
	 *            The world trying to operate on
	 */
	public StoreUnsupportedWorldException(final EWorld world) {
		super(world.toString());
	}

}
