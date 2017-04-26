package de.zabuza.beedlebot.store;

import java.io.Serializable;
import java.util.Optional;

/**
 * Holds item price data like the standard shop price or the player market price
 * of a given item.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ItemPrice implements Serializable, Cloneable {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Whether the item price data was retrieved from a cache or not.
	 */
	private boolean mIsCached;
	/**
	 * The timestamp of when the data represented by this object was retrieved
	 * from outside of a cache.
	 */
	private final long mLookupTimestamp;
	/**
	 * The name of the item this data belongs to.
	 */
	private final String mName;
	/**
	 * The player price of this item.
	 */
	private final PlayerPrice mPlayerPrice;
	/**
	 * The standard shop price of this item.
	 */
	private final int mStandardShopPrice;

	/**
	 * Creates a new item price data for the given item which has no player
	 * price.
	 * 
	 * @param name
	 *            The name of the item
	 * @param standardShopPrice
	 *            The standard shop price of the item
	 * @param isCached
	 *            Whether the item price data was retrieved from a cache or not
	 * @param lookupTimestamp
	 *            The timestamp of when the data represented by this object was
	 *            retrieved from outside of a cache
	 */
	public ItemPrice(final String name, final int standardShopPrice, final boolean isCached,
			final long lookupTimestamp) {
		this(name, standardShopPrice, null, isCached, lookupTimestamp);
	}

	/**
	 * Creates a new item price data for the given item.
	 * 
	 * @param name
	 *            The name of the item
	 * @param standardShopPrice
	 *            The standard shop price of the item
	 * @param playerPrice
	 *            The player price of the item
	 * @param isCached
	 *            Whether the item price data was retrieved from a cache or not
	 * @param lookupTimestamp
	 *            The timestamp of when the data represented by this object was
	 *            retrieved from outside of a cache
	 */
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

	/**
	 * Gets the timestamp of when the data represented by this object was
	 * retrieved from outside of a cache.
	 * 
	 * @return The timestamp of when the data represented by this object was
	 *         retrieved from outside of a cache
	 */
	public long getLookupTimestamp() {
		return this.mLookupTimestamp;
	}

	/**
	 * Gets the name of the item this data belongs to.
	 * 
	 * @return The name of the item this data belongs to
	 */
	public String getName() {
		return this.mName;
	}

	/**
	 * If present gets the player price of this item.
	 * 
	 * @return If present the player price of this item
	 */
	public Optional<PlayerPrice> getPlayerPrice() {
		if (hasPlayerPrice()) {
			return Optional.of(this.mPlayerPrice);
		}
		return Optional.empty();
	}

	/**
	 * Gets the standard shop price of this item.
	 * 
	 * @return The standard shop price of this item
	 */
	public int getStandardShopPrice() {
		return this.mStandardShopPrice;
	}

	/**
	 * Whether the item has a player price or not.
	 * 
	 * @return <tt>True</tt> if the item has a player price, <tt>false</tt> if
	 *         not
	 */
	public boolean hasPlayerPrice() {
		return this.mPlayerPrice != null;
	}

	/**
	 * Whether the item price data was retrieved from a cache or not.
	 * 
	 * @return <tt>True</tt> if the item price data was retrieved from a cache,
	 *         <tt>false</tt> if not
	 */
	public boolean isCached() {
		return this.mIsCached;
	}

	/**
	 * Sets whether the item price data was retrieved from a cache or not.
	 * 
	 * @param isCached
	 *            <tt>True</tt> if the item price data was retrieved from a
	 *            cache, <tt>false</tt> if not
	 */
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
