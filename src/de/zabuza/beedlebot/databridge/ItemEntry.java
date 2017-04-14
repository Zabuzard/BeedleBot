package de.zabuza.beedlebot.databridge;

public final class ItemEntry {
	private static long createTimestamp() {
		return System.currentTimeMillis();
	}

	private final int mCost;
	private final boolean mIsConsideredForShop;
	private final String mItem;
	private final int mProfit;
	private final long mTimestamp;
	private final boolean mWasCached;

	public ItemEntry(final String item, final int cost, final int profit, final boolean wasCached,
			final boolean isConsideredForShop) {
		mItem = item;
		mCost = cost;
		mProfit = profit;
		mWasCached = wasCached;
		mIsConsideredForShop = isConsideredForShop;
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

	public boolean isConsideredForShop() {
		return mIsConsideredForShop;
	}

	public boolean wasCached() {
		return mWasCached;
	}
}
