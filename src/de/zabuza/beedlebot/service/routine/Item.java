package de.zabuza.beedlebot.service.routine;

public final class Item {

	private final int mCost;
	private final EItemCategory mItemCategory;
	private final String mName;
	private final int mProfit;

	public Item(final String name, final int cost, final int profit, final EItemCategory itemCategory) {
		mName = name;
		mCost = cost;
		mProfit = profit;
		mItemCategory = itemCategory;
	}

	public int getCost() {
		return mCost;
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
}
