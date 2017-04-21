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
		this.mItem = item;
		this.mCost = cost;
		this.mProfit = profit;
		this.mWasCached = wasCached;
		this.mIsConsideredForShop = isConsideredForShop;
		this.mTimestamp = createTimestamp();
	}

	public int getCost() {
		return this.mCost;
	}

	public String getItem() {
		return this.mItem;
	}

	public int getProfit() {
		return this.mProfit;
	}

	public long getTimestamp() {
		return this.mTimestamp;
	}

	public boolean isConsideredForShop() {
		return this.mIsConsideredForShop;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ItemEntry [cost=");
		builder.append(this.mCost);
		builder.append(", isConsideredForShop=");
		builder.append(this.mIsConsideredForShop);
		builder.append(", ");
		if (this.mItem != null) {
			builder.append("item=");
			builder.append(this.mItem);
			builder.append(", ");
		}
		builder.append("profit=");
		builder.append(this.mProfit);
		builder.append(", timestamp=");
		builder.append(this.mTimestamp);
		builder.append(", wasCached=");
		builder.append(this.mWasCached);
		builder.append("]");
		return builder.toString();
	}

	public boolean wasCached() {
		return this.mWasCached;
	}
}
