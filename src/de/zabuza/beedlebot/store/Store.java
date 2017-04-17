package de.zabuza.beedlebot.store;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import de.zabuza.beedlebot.exceptions.NoStandardShopPriceException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.sparkle.freewar.EWorld;

public final class Store {
	private static final int CONSIDER_PLAYER_PRICE_COST_ABS = 200;
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

	private final ItemDictionary mItemDictionary;
	private final ILogger mLogger;
	private final PlayerPriceFinder mPlayerPriceFinder;
	private final PurchaseRegister mPurchaseRegister;
	private final StandardShopPriceFinder mStandardShopPriceFinder;
	private final StoreCache mStoreCache;
	private final EWorld mWorld;

	public Store(final String user, final EWorld world) {
		mLogger = LoggerFactory.getLogger();
		mWorld = world;
		mItemDictionary = new ItemDictionary();
		mStandardShopPriceFinder = new StandardShopPriceFinder(mItemDictionary);
		mPlayerPriceFinder = new PlayerPriceFinder(mItemDictionary);
		mPurchaseRegister = new PurchaseRegister(user, world);

		// Try to create cache from serialized content
		if (StoreCache.hasSerializedCache(mWorld)) {
			mStoreCache = StoreCache.deserialize(mWorld);
		} else {
			mStoreCache = new StoreCache(mWorld);
		}
	}

	public void finalize() {
		mStoreCache.serialize();
	}

	public ItemPrice getItemPrice(final String itemName) {
		return getItemPrice(itemName, false);
	}

	public boolean isItemAccepted(final Item item) {
		if (item.isMagical()) {
			return false;
		}

		return item.getProfit() > 0;
	}

	public boolean isItemConsideredForShop(final String itemName, final int cost, final ItemPrice itemPrice) {
		// First check the dictionary for exceptions
		if (mItemDictionary.isItemRegisteredForShop(itemName)) {
			return true;
		}
		if (mItemDictionary.isItemRegisteredForPlayer(itemName)) {
			return false;
		}

		final int standardShopPrice = itemPrice.getStandardShopPrice();
		final int shopPrice = Store.computeFullShopPrice(standardShopPrice);
		if (itemPrice.hasPlayerPrice()) {
			final int playerPrice = itemPrice.getPlayerPrice().get().getPrice();
			if (playerPrice - cost >= CONSIDER_PLAYER_PRICE_COST_ABS) {
				// Decide for the bigger one
				return shopPrice >= playerPrice;
			} else {
				// Decide for shop
				return true;
			}
		} else {
			// Decide for shop
			return true;
		}
	}

	public void registerItemPurchase(final Item item) {
		mPurchaseRegister.registerPurchase(item);
	}

	private ItemPrice getItemPrice(final String itemName, final boolean ignoreCache)
			throws NoStandardShopPriceException {
		if (mLogger.isDebugEnabled()) {
			mLogger.logDebug("Getting item price: " + itemName + ", " + ignoreCache);
		}

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
			final Optional<Integer> standardShopPrice = mStandardShopPriceFinder.findStandardShopPrice(itemName);

			if (!standardShopPrice.isPresent()) {
				throw new NoStandardShopPriceException(itemName);
			}

			// Lookup player to player price in MPLogger interface
			final Optional<PlayerPrice> playerPrice = mPlayerPriceFinder.findPlayerPrice(itemName, mWorld);

			// Create data
			if (playerPrice.isPresent()) {
				itemPrice = new ItemPrice(itemName, standardShopPrice.get(), playerPrice.get(), false,
						System.currentTimeMillis());
			} else {
				itemPrice = new ItemPrice(itemName, standardShopPrice.get(), false, System.currentTimeMillis());
			}

			// Create a version for the cache and update it
			final ItemPrice cacheItemPrice = itemPrice.clone();
			cacheItemPrice.setIsCached(true);
			mStoreCache.putItemPrice(cacheItemPrice);
		}

		return itemPrice;
	}
}
