package de.zabuza.beedlebot.store;

import java.io.Serializable;

import de.zabuza.sparkle.freewar.EWorld;

public final class PlayerPrice implements Serializable, Cloneable {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	private final int mPrice;
	private final long mTimestamp;
	private final EWorld mWorld;

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

	public int getPrice() {
		return this.mPrice;
	}

	public long getTimestamp() {
		return this.mTimestamp;
	}

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
