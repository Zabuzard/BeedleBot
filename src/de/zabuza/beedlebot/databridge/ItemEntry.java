package de.zabuza.beedlebot.databridge;

public final class ItemEntry {
	private static long createTimestamp() {
		return System.currentTimeMillis();
	}

	private final int mCost;
	private final String mItem;
	private final int mProfit;

	private final long mTimestamp;

	public ItemEntry(final String item, final int cost, final int profit) {
		mItem = item;
		mCost = cost;
		mProfit = profit;
		mTimestamp = createTimestamp();
	}

	public int getCost() {
		return mCost;
	}

	public String getItem() {
		return mItem;
	}

	public int getProfit() {
		return mProfit;
	}

	public long getTimestamp() {
		return mTimestamp;
	}
}
