package de.zabuza.beedlebot.store;

import java.util.concurrent.TimeUnit;

import de.zabuza.sparkle.freewar.EWorld;

public final class Store {

	private static final float FULL_SHOP_DISCOUNT_FACTOR = 1.14999f;
	private static final int STORED_ITEM_VALIDITY_DAYS = 30;

	public static int computeFullShopPrice(final int standardShopPrice) {
		return (int) Math.floor(standardShopPrice * FULL_SHOP_DISCOUNT_FACTOR);
	}
	private static boolean isItemPriceValid(final ItemPrice itemPrice) {
		final long timestamp = itemPrice.getPlayerPrice().getTimestamp();
		final long now = System.currentTimeMillis();
		final long diff = now - timestamp;

		return TimeUnit.MILLISECONDS.toDays(diff) <= STORED_ITEM_VALIDITY_DAYS;
	}

	private final StoreCache mStoreCache;

	private final EWorld mWorld;

	public Store(final EWorld world) {
		mWorld = world;
		// TODO Try to create the cache with deserialization
		mStoreCache = new StoreCache(mWorld);
	}

	public ItemPrice getItemPrice(final String itemName) {
		ItemPrice itemPrice = null;
		// Try to use the cache first
		if (mStoreCache.hasItemPrice(itemName)) {
			ItemPrice storedItemPrice = mStoreCache.getItemPrice(itemName);
			if (isItemPriceValid(storedItemPrice)) {
				itemPrice = storedItemPrice;
			}
		}

		// Lookup the price if item is not cached or not valid
		if (itemPrice == null) {
			// Lookup standard price in Fwwiki
			// TODO Implement and remove dummy
			int shopPrice = 0;

			// TODO Correct error handling and logging, e.g. if item is unknown

			// Lookup player to player price in MPLogger interface
			// TODO Implement and remove dummy
			final PlayerPrice playerPrice = new PlayerPrice(0, System.currentTimeMillis(), mWorld);

			// Create data and update the cache
			itemPrice = new ItemPrice(itemName, shopPrice, playerPrice);
			mStoreCache.putItemPrice(itemPrice);
		}

		return itemPrice;
	}
}
