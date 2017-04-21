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
	private final int mStandardShopPrice;

	public ItemPrice(final String name, final int standardShopPrice, final boolean isCached,
			final long lookupTimestamp) {
		this(name, standardShopPrice, null, isCached, lookupTimestamp);
	}

	public ItemPrice(final String name, final int standardShopPrice, final PlayerPrice playerPrice,
			final boolean isCached, final long lookupTimestamp) {
		this.mName = name;
		this.mStandardShopPrice = standardShopPrice;
		this.mPlayerPrice = playerPrice;
		this.mLookupTimestamp = lookupTimestamp;
		this.mIsCached = isCached;
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
			return new ItemPrice(getName(), getStandardShopPrice(), clonedPlayerPrice, isCached(),
					getLookupTimestamp());
		}
		return new ItemPrice(getName(), getStandardShopPrice(), isCached(), getLookupTimestamp());
	}

	public long getLookupTimestamp() {
		return this.mLookupTimestamp;
	}

	public String getName() {
		return this.mName;
	}

	public Optional<PlayerPrice> getPlayerPrice() {
		if (hasPlayerPrice()) {
			return Optional.of(this.mPlayerPrice);
		}
		return Optional.empty();
	}

	public int getStandardShopPrice() {
		return this.mStandardShopPrice;
	}

	public boolean hasPlayerPrice() {
		return this.mPlayerPrice != null;
	}

	public boolean isCached() {
		return this.mIsCached;
	}

	public void setIsCached(final boolean isCached) {
		this.mIsCached = isCached;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ItemPrice [isCached=");
		builder.append(this.mIsCached);
		builder.append(", lookupTimestamp=");
		builder.append(this.mLookupTimestamp);
		builder.append(", ");
		if (this.mName != null) {
			builder.append("name=");
			builder.append(this.mName);
			builder.append(", ");
		}
		if (this.mPlayerPrice != null) {
			builder.append("playerPrice=");
			builder.append(this.mPlayerPrice);
			builder.append(", ");
		}
		builder.append("standardShopPrice=");
		builder.append(this.mStandardShopPrice);
		builder.append("]");
		return builder.toString();
	}
}
