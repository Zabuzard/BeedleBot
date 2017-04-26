package de.zabuza.beedlebot.databridge;

/**
 * Represents a Freewar item for use in the communication channel.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ItemEntry {
	/**
	 * Creates a timestamp of the current time.
	 * 
	 * @return A timestamp of the current time
	 */
	private static long createTimestamp() {
		return System.currentTimeMillis();
	}

	/**
	 * The cost of the item.
	 */
	private final int mCost;
	/**
	 * Whether the item is considered to get sold to the shop or to players.
	 */
	private final boolean mIsConsideredForShop;
	/**
	 * The name of the item.
	 */
	private final String mItem;
	/**
	 * The expected profit of the item which is
	 * <tt>cost - expected sell price</tt>.
	 */
	private final int mProfit;
	/**
	 * The timestamp of when the item was bought.
	 */
	private final long mTimestamp;
	/**
	 * Whether the item price data was cached or not.
	 */
	private final boolean mWasCached;

	/**
	 * Creates a new item entry object that represents the item with the given
	 * data.
	 * 
	 * @param item
	 *            The name of the item
	 * @param cost
	 *            The cost of the item
	 * @param profit
	 *            The expected profit of the item which is
	 *            <tt>cost - expected sell price</tt>
	 * @param wasCached
	 *            Whether the item price data was cached or not
	 * @param isConsideredForShop
	 *            Whether the item is considered to get sold to the shop or to
	 *            players
	 */
	public ItemEntry(final String item, final int cost, final int profit, final boolean wasCached,
			final boolean isConsideredForShop) {
		this.mItem = item;
		this.mCost = cost;
		this.mProfit = profit;
		this.mWasCached = wasCached;
		this.mIsConsideredForShop = isConsideredForShop;
		this.mTimestamp = createTimestamp();
	}

	/**
	 * Gets the cost of the item.
	 * 
	 * @return The cost of the item
	 */
	public int getCost() {
		return this.mCost;
	}

	/**
	 * Gets the name of the item.
	 * 
	 * @return The name of the item
	 */
	public String getItem() {
		return this.mItem;
	}

	/**
	 * Gets the expected profit of the item which is
	 * <tt>cost - expected sell price</tt>.
	 * 
	 * @return The expected profit of the item
	 */
	public int getProfit() {
		return this.mProfit;
	}

	/**
	 * Gets the timestamp of when the item was bought.
	 * 
	 * @return The timestamp of when the item was bought
	 */
	public long getTimestamp() {
		return this.mTimestamp;
	}

	/**
	 * Whether the item is considered to get sold to the shop or to players.
	 * 
	 * @return <tt>True</tt> if the item is considered to get sold to the shop,
	 *         <tt>false</tt> if it is considered to get sold to players.
	 */
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

	/**
	 * Whether the item price data was cached or not.
	 * 
	 * @return <tt>True</tt> if the item price data was cached, <tt>false</tt>
	 *         if not.
	 */
	public boolean wasCached() {
		return this.mWasCached;
	}
}
