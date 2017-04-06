package de.zabuza.beedlebot.store;

import java.util.Optional;

public final class ItemPrice {
	private final long mLookupTimestamp;
	private final String mName;
	private final Optional<PlayerPrice> mPlayerPrice;
	private final int mShopPrice;

	public ItemPrice(final String name, final int shopPrice, final Optional<PlayerPrice> playerPrice,
			final long lookupTimestamp) {
		mName = name;
		mShopPrice = shopPrice;
		mPlayerPrice = playerPrice;
		mLookupTimestamp = lookupTimestamp;
	}

	public long getLookupTimestamp() {
		return mLookupTimestamp;
	}

	public String getName() {
		return mName;
	}

	public Optional<PlayerPrice> getPlayerPrice() {
		return mPlayerPrice;
	}

	public int getShopPrice() {
		return mShopPrice;
	}

	public boolean hasPlayerPrice() {
		return mPlayerPrice.isPresent();
	}
}
