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
		mPrice = price;
		mTimestamp = timestamp;
		mWorld = world;
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
		return mPrice;
	}

	public long getTimestamp() {
		return mTimestamp;
	}

	public EWorld getWorld() {
		return mWorld;
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
		builder.append(mPrice);
		builder.append(", timestamp=");
		builder.append(mTimestamp);
		builder.append(", ");
		if (mWorld != null) {
			builder.append("world=");
			builder.append(mWorld);
		}
		builder.append("]");
		return builder.toString();
	}
}
