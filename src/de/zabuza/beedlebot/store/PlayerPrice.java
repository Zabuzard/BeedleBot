package de.zabuza.beedlebot.store;

import de.zabuza.sparkle.freewar.EWorld;

public final class PlayerPrice {
	private final int mPrice;
	private final long mTimestamp;
	private final EWorld mWorld;

	public PlayerPrice(final int price, final long timestamp, final EWorld world) {
		mPrice = price;
		mTimestamp = timestamp;
		mWorld = world;
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
}
