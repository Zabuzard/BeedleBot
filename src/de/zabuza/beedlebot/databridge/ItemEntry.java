package de.zabuza.beedlebot.databridge;

public final class ItemEntry {
	private final String mItem;
	private final int mCost;
	private final int mProfit;
	private final long mTimestamp;

	public ItemEntry(final String item, final int cost, final int profit) {
		mItem = item;
		mCost = cost;
		mProfit = profit;
		mTimestamp = createTimestamp();
	}

	private static long createTimestamp() {
		return System.currentTimeMillis();
	}

	public String getItem() {
		return mItem;
	}

	public int getCost() {
		return mCost;
	}

	public int getProfit() {
		return mProfit;
	}

	public long getTimestamp() {
		return mTimestamp;
	}
}
