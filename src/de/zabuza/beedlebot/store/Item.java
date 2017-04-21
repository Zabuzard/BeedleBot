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

	public int getCost() {
		return this.mCost;
	}

	public int getId() {
		return this.mId;
	}

	public EItemCategory getItemCategory() {
		return this.mItemCategory;
	}

	public String getName() {
		return this.mName;
	}

	public int getProfit() {
		return this.mProfit;
	}

	public String getPurchaseAnchor() {
		return this.mPurchaseAnchor;
	}

	public ItemPrice getStorePriceData() {
		return this.mStorePriceData;
	}

	public boolean isConsideredForShop() {
		return this.mIsConsideredForShop;
	}

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
