package de.zabuza.beedlebot.service.routine;

import de.zabuza.beedlebot.store.ItemPrice;

public final class Item {

	private final int mCost;
	private final EItemCategory mItemCategory;
	private final String mName;
	private final int mProfit;
	private final ItemPrice mStorePriceData;

	public Item(final String name, final int cost, final int profit, final ItemPrice storePriceData,
			final EItemCategory itemCategory) {
		mName = name;
		mCost = cost;
		mProfit = profit;
		mItemCategory = itemCategory;
		mStorePriceData = storePriceData;
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

	public ItemPrice getStorePriceData() {
		return mStorePriceData;
	}
}
