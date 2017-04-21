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

	public static boolean isItemAccepted(final Item item) {
		if (item.isMagical()) {
			return false;
		}
		if (item.getName().equals("gepresste Zauberkugel")) {
			return false;
		}

		return item.getProfit() > 0;
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
		this.mLogger = LoggerFactory.getLogger();
		this.mWorld = world;
		this.mItemDictionary = new ItemDictionary();
		this.mStandardShopPriceFinder = new StandardShopPriceFinder(this.mItemDictionary);
		this.mPlayerPriceFinder = new PlayerPriceFinder(this.mItemDictionary);
		this.mPurchaseRegister = new PurchaseRegister(user, world);

		// Try to create cache from serialized content
		if (StoreCache.hasSerializedCache(this.mWorld)) {
			this.mStoreCache = StoreCache.deserialize(this.mWorld);
		} else {
			this.mStoreCache = new StoreCache(this.mWorld);
		}
	}

	public ItemPrice getItemPrice(final String itemName) {
		return getItemPrice(itemName, false);
	}

	public boolean isItemConsideredForShop(final String itemName, final int cost, final ItemPrice itemPrice,
			final EItemCategory category) {
		// First check the dictionary for exceptions
		if (this.mItemDictionary.isItemRegisteredForShop(itemName, category)) {
			return true;
		}
		if (this.mItemDictionary.isItemRegisteredForPlayer(itemName)) {
			return false;
		}

		final int standardShopPrice = itemPrice.getStandardShopPrice();
		final int shopPrice = Store.computeFullShopPrice(standardShopPrice);
		if (itemPrice.hasPlayerPrice()) {
			final int playerPrice = itemPrice.getPlayerPrice().get().getPrice();
			if (playerPrice - cost >= CONSIDER_PLAYER_PRICE_COST_ABS) {
				// Decide for the bigger one
				return shopPrice >= playerPrice;
			}
			// Decide for shop
			return true;
		}
		// Decide for shop
		return true;
	}

	public void registerItemPurchase(final Item item) {
		this.mPurchaseRegister.registerPurchase(item);
	}

	public void shutdown() {
		this.mStoreCache.serialize();
	}

	private ItemPrice getItemPrice(final String itemName, final boolean ignoreCache)
			throws NoStandardShopPriceException {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Getting item price: " + itemName + ", " + ignoreCache);
		}

		ItemPrice itemPrice = null;
		// Try to use the cache first
		if (!ignoreCache && this.mStoreCache.hasItemPrice(itemName)) {
			final ItemPrice storedItemPrice = this.mStoreCache.getItemPrice(itemName);
			if (isItemPriceValid(storedItemPrice)) {
				itemPrice = storedItemPrice;
			}
		}

		// Lookup the price if item is not cached or not valid
		if (itemPrice == null) {
			// Lookup standard price in FwWiki
			final Optional<Integer> standardShopPrice = this.mStandardShopPriceFinder.findStandardShopPrice(itemName);

			if (!standardShopPrice.isPresent()) {
				throw new NoStandardShopPriceException(itemName);
			}

			// Lookup player to player price in MPLogger interface
			final Optional<PlayerPrice> playerPrice = this.mPlayerPriceFinder.findPlayerPrice(itemName, this.mWorld);

			// Create data
			if (playerPrice.isPresent()) {
				itemPrice = new ItemPrice(itemName, standardShopPrice.get().intValue(), playerPrice.get(), false,
						System.currentTimeMillis());
			} else {
				itemPrice = new ItemPrice(itemName, standardShopPrice.get().intValue(), false,
						System.currentTimeMillis());
			}

			// Create a version for the cache and update it
			final ItemPrice cacheItemPrice = itemPrice.clone();
			cacheItemPrice.setIsCached(true);
			this.mStoreCache.putItemPrice(cacheItemPrice);
		}

		return itemPrice;
	}
}
