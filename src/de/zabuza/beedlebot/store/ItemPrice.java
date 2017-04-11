package de.zabuza.beedlebot.store;

import java.io.Serializable;
import java.util.Optional;

public final class ItemPrice implements Serializable, Cloneable {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	private boolean mIsCached;
	private final long mLookupTimestamp;
	private final String mName;
	private final PlayerPrice mPlayerPrice;
	private final int mShopPrice;

	public ItemPrice(final String name, final int shopPrice, final boolean isCached, final long lookupTimestamp) {
		this(name, shopPrice, null, isCached, lookupTimestamp);
	}

	public ItemPrice(final String name, final int shopPrice, final PlayerPrice playerPrice, final boolean isCached,
			final long lookupTimestamp) {
		mName = name;
		mShopPrice = shopPrice;
		mPlayerPrice = playerPrice;
		mLookupTimestamp = lookupTimestamp;
		mIsCached = isCached;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ItemPrice clone() {
		final Optional<PlayerPrice> playerPrice = getPlayerPrice();
		if (playerPrice.isPresent()) {
			final PlayerPrice clonedPlayerPrice = playerPrice.get().clone();
			return new ItemPrice(getName(), getShopPrice(), clonedPlayerPrice, isCached(), getLookupTimestamp());
		} else {
			return new ItemPrice(getName(), getShopPrice(), isCached(), getLookupTimestamp());
		}
	}

	public long getLookupTimestamp() {
		return mLookupTimestamp;
	}

	public String getName() {
		return mName;
	}

	public Optional<PlayerPrice> getPlayerPrice() {
		if (hasPlayerPrice()) {
			return Optional.of(mPlayerPrice);
		} else {
			return Optional.empty();
		}
	}

	public int getShopPrice() {
		return mShopPrice;
	}

	public boolean hasPlayerPrice() {
		return mPlayerPrice != null;
	}

	public boolean isCached() {
		return mIsCached;
	}

	public void setIsCached(final boolean isCached) {
		mIsCached = isCached;
	}
}
