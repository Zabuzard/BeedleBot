package de.zabuza.beedlebot.store;

/**
 * Class that represents an item at the central traders depot with full price
 * data.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class Item {
	/**
	 * The cost of the item at the central traders depot.
	 */
	private final int mCost;
	/**
	 * The id of the item in the game.
	 */
	private final int mId;
	/**
	 * Whether the item is considered to be sold to the shop or to players.
	 */
	private final boolean mIsConsideredForShop;
	/**
	 * Whether the item is a magical item or not.
	 */
	private final boolean mIsMagical;
	/**
	 * The category of the item at the central traders depot.
	 */
	private final EItemCategory mItemCategory;
	/**
	 * The name of the item.
	 */
	private final String mName;
	/**
	 * The expected profit of the item which is
	 * <tt>cost - expected sell price</tt>.
	 */
	private final int mProfit;
	/**
	 * The anchor reference to purchase the item from the central traders depot.
	 */
	private final String mPurchaseAnchor;
	/**
	 * The price data of this item from the {@link Store}.
	 */
	private final ItemPrice mStorePriceData;

	/**
	 * Creates a new item that represents the item with the given data at the
	 * central traders depot.
	 * 
	 * @param name
	 *            The name of the item
	 * @param cost
	 *            The cost of the item at the central traders depot
	 * @param profit
	 *            The expected profit of the item which is
	 *            <tt>cost - expected sell price</tt>.
	 * @param id
	 *            The id of the item in the game
	 * @param purchaseAnchor
	 *            The anchor reference to purchase the item from the central
	 *            traders depot
	 * @param isMagical
	 *            Whether the item is a magical item or not
	 * @param isConsideredForShop
	 *            Whether the item is considered to be sold to the shop or to
	 *            players
	 * @param storePriceData
	 *            The price data of this item from the {@link Store}
	 * @param itemCategory
	 *            The category of the item at the central traders depot
	 */
	public Item(final String name, final int cost, final int profit, final int id, final String purchaseAnchor,
			final boolean isMagical, final boolean isConsideredForShop, final ItemPrice storePriceData,
			final EItemCategory itemCategory) {
		this.mName = name;
		this.mCost = cost;
		this.mProfit = profit;
		this.mId = id;
		this.mItemCategory = itemCategory;
		this.mStorePriceData = storePriceData;
		this.mPurchaseAnchor = purchaseAnchor;
		this.mIsMagical = isMagical;
		this.mIsConsideredForShop = isConsideredForShop;
	}

	/**
	 * Gets the cost of the item at the central traders depot
	 * 
	 * @return The cost of the item at the central traders depot
	 */
	public int getCost() {
		return this.mCost;
	}

	/**
	 * Gets the id of the item in the game.
	 * 
	 * @return The id of the item in the game
	 */
	public int getId() {
		return this.mId;
	}

	/**
	 * Gets the category of the item at the central traders depot.
	 * 
	 * @return The category of the item at the central traders depot
	 */
	public EItemCategory getItemCategory() {
		return this.mItemCategory;
	}

	/**
	 * Gets the name of the item.
	 * 
	 * @return The name of the item
	 */
	public String getName() {
		return this.mName;
	}

	/**
	 * Gets the expected profit of the item which is
	 * <tt>cost - expected sell price</tt>.
	 * 
	 * @return The expected profit of the item which is
	 *         <tt>cost - expected sell price</tt>
	 */
	public int getProfit() {
		return this.mProfit;
	}

	/**
	 * Gets the anchor reference to purchase the item from the central traders
	 * depot.
	 * 
	 * @return The anchor reference to purchase the item from the central
	 *         traders depot
	 */
	public String getPurchaseAnchor() {
		return this.mPurchaseAnchor;
	}

	/**
	 * Gets the price data of this item.
	 * 
	 * @return The price data of this item
	 */
	public ItemPrice getStorePriceData() {
		return this.mStorePriceData;
	}

	/**
	 * Whether the item is considered to be sold to the shop or to players.
	 * 
	 * @return <tt>True</tt> if the item is considered to be sold to the shop,
	 *         <tt>false</tt> if it is considered to be sold to players
	 */
	public boolean isConsideredForShop() {
		return this.mIsConsideredForShop;
	}

	/**
	 * Whether the item is a magical item or not.
	 * 
	 * @return <tt>True</tt> if the item is a magical item, <tt>false</tt> if
	 *         not
	 */
	public boolean isMagical() {
		return this.mIsMagical;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Item [cost=");
		builder.append(this.mCost);
		builder.append(", id=");
		builder.append(this.mId);
		builder.append(", isConsideredForShop=");
		builder.append(this.mIsConsideredForShop);
		builder.append(", isMagical=");
		builder.append(this.mIsMagical);
		builder.append(", ");
		if (this.mItemCategory != null) {
			builder.append("itemCategory=");
			builder.append(this.mItemCategory);
			builder.append(", ");
		}
		if (this.mName != null) {
			builder.append("name=");
			builder.append(this.mName);
			builder.append(", ");
		}
		builder.append("profit=");
		builder.append(this.mProfit);
		builder.append(", ");
		if (this.mPurchaseAnchor != null) {
			builder.append("purchaseAnchor=");
			builder.append(this.mPurchaseAnchor);
			builder.append(", ");
		}
		if (this.mStorePriceData != null) {
			builder.append("storePriceData=");
			builder.append(this.mStorePriceData);
		}
		builder.append("]");
		return builder.toString();
	}
}
