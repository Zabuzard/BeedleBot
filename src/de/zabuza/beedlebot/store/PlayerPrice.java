package de.zabuza.beedlebot.store;

import java.io.Serializable;

import de.zabuza.sparkle.freewar.EWorld;

/**
 * Holds player price data for items.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class PlayerPrice implements Serializable, Cloneable {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The player price of the item.
	 */
	private final int mPrice;
	/**
	 * The timestamp of when this player price was up-to-date.
	 */
	private final long mTimestamp;
	/**
	 * The world the player price belongs to.
	 */
	private final EWorld mWorld;

	/**
	 * Creates a new player price for an item.
	 * 
	 * @param price
	 *            The player price of the item
	 * @param timestamp
	 *            The timestamp of when this player price was up-to-date
	 * @param world
	 *            The world the player price belongs to
	 */
	public PlayerPrice(final int price, final long timestamp, final EWorld world) {
		this.mPrice = price;
		this.mTimestamp = timestamp;
		this.mWorld = world;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PlayerPrice clone() {
		return new PlayerPrice(getPrice(), getTimestamp(), getWorld());
	}

	/**
	 * Gets the player price of the item.
	 * 
	 * @return The player price of the item
	 */
	public int getPrice() {
		return this.mPrice;
	}

	/**
	 * Gets the timestamp of when this player price was up-to-date.
	 * 
	 * @return The timestamp of when this player price was up-to-date
	 */
	public long getTimestamp() {
		return this.mTimestamp;
	}

	/**
	 * Gets the world the player price belongs to.
	 * 
	 * @return The world the player price belongs to
	 */
	public EWorld getWorld() {
		return this.mWorld;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("PlayerPrice [price=");
		builder.append(this.mPrice);
		builder.append(", timestamp=");
		builder.append(this.mTimestamp);
		builder.append(", ");
		if (this.mWorld != null) {
			builder.append("world=");
			builder.append(this.mWorld);
		}
		builder.append("]");
		return builder.toString();
	}
}
