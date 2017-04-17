package de.zabuza.beedlebot.store;

public final class Item {

	private final int mCost;
	private final int mId;
	private final boolean mIsConsideredForShop;
	private final boolean mIsMagical;
	private final EItemCategory mItemCategory;
	private final String mName;
	private final int mProfit;
	private final String mPurchaseAnchor;
	private final ItemPrice mStorePriceData;

	public Item(final String name, final int cost, final int profit, final int id, final String purchaseAnchor,
			boolean isMagical, final boolean isConsideredForShop, final ItemPrice storePriceData,
			final EItemCategory itemCategory) {
		mName = name;
		mCost = cost;
		mProfit = profit;
		mId = id;
		mItemCategory = itemCategory;
		mStorePriceData = storePriceData;
		mPurchaseAnchor = purchaseAnchor;
		mIsMagical = isMagical;
		mIsConsideredForShop = isConsideredForShop;
	}

	public int getCost() {
		return mCost;
	}

	public int getId() {
		return mId;
	}

	public EItemCategory getItemCategory() {
		return mItemCategory;
	}

	public String getName() {
		return mName;
	}

	public int getProfit() {
		return mProfit;
	}

	public String getPurchaseAnchor() {
		return mPurchaseAnchor;
	}

	public ItemPrice getStorePriceData() {
		return mStorePriceData;
	}

	public boolean isConsideredForShop() {
		return mIsConsideredForShop;
	}

	public boolean isMagical() {
		return mIsMagical;
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
		builder.append(mCost);
		builder.append(", id=");
		builder.append(mId);
		builder.append(", isConsideredForShop=");
		builder.append(mIsConsideredForShop);
		builder.append(", isMagical=");
		builder.append(mIsMagical);
		builder.append(", ");
		if (mItemCategory != null) {
			builder.append("itemCategory=");
			builder.append(mItemCategory);
			builder.append(", ");
		}
		if (mName != null) {
			builder.append("name=");
			builder.append(mName);
			builder.append(", ");
		}
		builder.append("profit=");
		builder.append(mProfit);
		builder.append(", ");
		if (mPurchaseAnchor != null) {
			builder.append("purchaseAnchor=");
			builder.append(mPurchaseAnchor);
			builder.append(", ");
		}
		if (mStorePriceData != null) {
			builder.append("storePriceData=");
			builder.append(mStorePriceData);
		}
		builder.append("]");
		return builder.toString();
	}
}
