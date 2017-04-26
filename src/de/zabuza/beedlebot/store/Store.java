package de.zabuza.beedlebot.store;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import de.zabuza.beedlebot.exceptions.NoStandardShopPriceException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.sparkle.freewar.EWorld;

/**
 * Store that contains methods for accessing item price data and registering
 * item purchases. Internally uses a cache system, use {@link #save()} to save
 * the store and {@link #shutdown()} to shut it down.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class Store {
	/**
	 * The absolute difference between the cost of an item and its player price
	 * that determines when such an item is considered to be sold to players.
	 */
	private static final int CONSIDER_PLAYER_PRICE_COST_ABS = 200;
	/**
	 * The expected discount factor of shops when selling items. Also used to
	 * compute whether an item is accepted to be purchased or not.
	 */
	private static final double SHOP_DISCOUNT_FACTOR = 1.14;
	/**
	 * The validity of cached items in days. Whenever it expires an item price
	 * request will not be fetched from the cache.
	 */
	private static final int STORED_ITEM_LOOKUP_VALIDITY_DAYS = 10;
	/**
	 * The validity of player price data in days. Whenever it expires an player
	 * price request will not be considered.
	 */
	private static final int STORED_ITEM_PLAYER_PRICE_VALIDITY_DAYS = 30;

	/**
	 * Computes the full shop price of the given standard shop price when the
	 * shop has the expected shop discount factor of
	 * {@link #SHOP_DISCOUNT_FACTOR}.
	 * 
	 * @param standardShopPrice
	 *            The standard shop price to use for computation
	 * @return The full shop price of the given standard shop price when the
	 *         shop has the expected shop discount factor of
	 *         {@link #SHOP_DISCOUNT_FACTOR}
	 */
	public static int computeShopPriceWithDiscount(final int standardShopPrice) {
		return (int) Math.floor(standardShopPrice * SHOP_DISCOUNT_FACTOR);
	}

	/**
	 * Whether the given item is accepted for purchase or not.
	 * 
	 * @param item
	 *            The item in question
	 * @return <tt>True</tt> if the given item is accepted for purchase,
	 *         <tt>false</tt> if not
	 */
	public static boolean isItemAcceptedForPurchase(final Item item) {
		if (item.isMagical()) {
			return false;
		}
		if (item.getName().equals("gepresste Zauberkugel")) {
			return false;
		}

		return item.getProfit() > 0;
	}

	/**
	 * Whether the given item price data is valid or not. If it is not valid a
	 * cached value should be rejected and marked as expired.
	 * 
	 * @param itemPrice
	 *            The item price in question
	 * @return <tt>True</tt> if the given item price is valid, <tt>false</tt> if
	 *         not
	 */
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

	/**
	 * The dictionary to use for exceptional items.
	 */
	private final ItemDictionary mItemDictionary;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;
	/**
	 * The service to use for finding player prices of items.
	 */
	private final PlayerPriceFinder mPlayerPriceFinder;
	/**
	 * The service to use for registering item purchases.
	 */
	private final PurchaseRegister mPurchaseRegister;
	/**
	 * The service to use for finding standard shop prices of items.
	 */
	private final StandardShopPriceFinder mStandardShopPriceFinder;
	/**
	 * The cache to use for storing and retrieving item price data.
	 */
	private final StoreCache mStoreCache;
	/**
	 * The world to use for player price computation.
	 */
	private final EWorld mWorld;

	/**
	 * Creates a new store that contains methods for accessing item price data
	 * and registering item purchases. Internally uses a cache system, use
	 * {@link #save()} to save the store and {@link #shutdown()} to shut it
	 * down.
	 * 
	 * @param user
	 *            The user that purchases items
	 * @param world
	 *            The world of the user that purchases items
	 */
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

	/**
	 * Gets item price data for the item with the given name.
	 * 
	 * @param itemName
	 *            The name of the item to get its price data
	 * @return The item price data for the item with the given name
	 * @throws NoStandardShopPriceException
	 *             When the given item has no standard shop price though every
	 *             item needs to have such a price
	 */
	public ItemPrice getItemPrice(final String itemName) throws NoStandardShopPriceException {
		return getItemPrice(itemName, false);
	}

	/**
	 * Whether the given item is considered to be sold to the shop or to
	 * players.
	 * 
	 * @param itemName
	 *            The name of the item in question
	 * @param cost
	 *            The cost of the item
	 * @param itemPrice
	 *            The price data of the item
	 * @return <tt>True</tt> if the given item is considered to be sold to the
	 *         shop, <tt>false</tt> if it is considered to be sold to players
	 */
	public boolean isItemConsideredForShop(final String itemName, final int cost, final ItemPrice itemPrice) {
		// First check the dictionary for exceptions
		if (this.mItemDictionary.isItemRegisteredForShop(itemName)) {
			return true;
		}
		if (this.mItemDictionary.isItemRegisteredForPlayer(itemName)) {
			return false;
		}

		final int standardShopPrice = itemPrice.getStandardShopPrice();
		final int shopPrice = Store.computeShopPriceWithDiscount(standardShopPrice);
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

	/**
	 * Registers the purchase of the given item.
	 * 
	 * @param item
	 *            The item whose purchase is to be registered
	 */
	public void registerItemPurchase(final Item item) {
		this.mPurchaseRegister.registerPurchase(item);
	}

	/**
	 * Saves the store data like serializing cache data.
	 */
	public void save() {
		this.mStoreCache.serialize();
	}

	/**
	 * Shuts the store down. The method will automatically call {@link #save()}
	 * at shutdown. Afterwards this object should not be used anymore, instead
	 * create a new instance.
	 */
	public void shutdown() {
		save();
	}

	/**
	 * Gets item price data for the item with the given name.
	 * 
	 * @param itemName
	 *            The name of the item to get its price data
	 * @param ignoreCache
	 *            <tt>True</tt> if the item price data cache should be ignored,
	 *            <tt>false</tt> if not
	 * @return The item price data for the item with the given name
	 * @throws NoStandardShopPriceException
	 *             When the given item has no standard shop price though every
	 *             item needs to have such a price
	 */
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
