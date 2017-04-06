package de.zabuza.beedlebot.store;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import de.zabuza.sparkle.freewar.EWorld;

public final class Store {

	private static final double FULL_SHOP_DISCOUNT_FACTOR = 1.15;
	private static final int STORED_ITEM_LOOKUP_VALIDITY_DAYS = 10;
	private static final int STORED_ITEM_PLAYER_PRICE_VALIDITY_DAYS = 30;

	public static int computeFullShopPrice(final int standardShopPrice) {
		return (int) Math.floor(standardShopPrice * FULL_SHOP_DISCOUNT_FACTOR);
	}

	private static boolean isItemPriceValid(final ItemPrice itemPrice) {
		final long now = System.currentTimeMillis();

		// First check lookup validity
		final long lookupTimestamp = itemPrice.getLookupTimestamp();
		final long lookupDiff = now - lookupTimestamp;

		final boolean isLookupValid = TimeUnit.MILLISECONDS.toDays(lookupDiff) <= STORED_ITEM_LOOKUP_VALIDITY_DAYS;

		if (isLookupValid) {
			return true;
		}

		// Check player to player price validity
		final long playerTimestamp = itemPrice.getPlayerPrice().get().getTimestamp();
		final long playerDiff = now - playerTimestamp;

		return TimeUnit.MILLISECONDS.toDays(playerDiff) <= STORED_ITEM_PLAYER_PRICE_VALIDITY_DAYS;
	}

	private final PlayerPriceFinder mPlayerPriceFinder;

	private final StandardShopPriceFinder mStandardShopPriceFinder;

	private final StoreCache mStoreCache;

	private final EWorld mWorld;

	public Store(final EWorld world) {
		mWorld = world;
		// TODO Try to create the cache with deserialization
		mStoreCache = new StoreCache(mWorld);
		mStandardShopPriceFinder = new StandardShopPriceFinder();
		mPlayerPriceFinder = new PlayerPriceFinder();
	}

	public ItemPrice getItemPrice(final String itemName) {
		return getItemPrice(itemName, false);
	}

	private ItemPrice getItemPrice(final String itemName, final boolean ignoreCache) {
		ItemPrice itemPrice = null;
		// Try to use the cache first
		if (!ignoreCache && mStoreCache.hasItemPrice(itemName)) {
			ItemPrice storedItemPrice = mStoreCache.getItemPrice(itemName);
			if (isItemPriceValid(storedItemPrice)) {
				itemPrice = storedItemPrice;
			}
		}

		// Lookup the price if item is not cached or not valid
		if (itemPrice == null) {
			// Lookup standard price in FwWiki
			final Optional<Integer> shopPrice = mStandardShopPriceFinder.findStandardShopPrice(itemName);

			if (!shopPrice.isPresent()) {
				// TODO Correct error handling and logging
				throw new IllegalArgumentException();
			}

			// Lookup player to player price in MPLogger interface
			final Optional<PlayerPrice> playerPrice = mPlayerPriceFinder.findPlayerPrice(itemName, mWorld);

			// Create data and update the cache
			itemPrice = new ItemPrice(itemName, shopPrice.get(), playerPrice, System.currentTimeMillis());
			mStoreCache.putItemPrice(itemPrice);
		}

		return itemPrice;
	}
}