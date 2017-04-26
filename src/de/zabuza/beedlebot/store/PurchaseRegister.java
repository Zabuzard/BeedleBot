package de.zabuza.beedlebot.store;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import de.zabuza.beedlebot.exceptions.PurchaseRegisterServiceUnavailableException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.sparkle.freewar.EWorld;

/**
 * Service that registers item purchases in a database.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class PurchaseRegister {
	/**
	 * The argument for the cost of a purchased item.
	 */
	private static final String ARG_COST = "cost";
	/**
	 * The argument for the id of a purchased item.
	 */
	private static final String ARG_ID = "id";
	/**
	 * The argument of whether the purchased item is considered to be sold to
	 * the shop or not.
	 */
	private static final String ARG_IS_CONSIDERED_FOR_SHOP = "isConsideredForShop";
	/**
	 * The argument for the name of the purchased item.
	 */
	private static final String ARG_ITEM = "item";
	/**
	 * The argument for the player price of the purchased item.
	 */
	private static final String ARG_PLAYER_PRICE = "playerPrice";
	/**
	 * The argument for the expected profit of the purchased item.
	 */
	private static final String ARG_PROFIT = "profit";
	/**
	 * The argument for the standard shop price of the purchased item.
	 */
	private static final String ARG_STANDARD_SHOP_PRICE = "standardShopPrice";
	/**
	 * The argument for the timestamp of when the price data was retrieved from
	 * outside of a cache.
	 */
	private static final String ARG_TS_CACHE = "ts_cache";
	/**
	 * The argument for the timestamp of when the player price data was
	 * up-to-date.
	 */
	private static final String ARG_TS_PLAYER_PRICE = "ts_playerPrice";
	/**
	 * The argument for the user name that purchased the item.
	 */
	private static final String ARG_USER = "user";
	/**
	 * The argument for whether the item price data was retrieved from a cache
	 * or not.
	 */
	private static final String ARG_WAS_CACHED = "wasCached";
	/**
	 * The argument for the world the player price data of the item belongs to.
	 */
	private static final String ARG_WORLD = "world";
	/**
	 * The path to the file that offers the service on the server.
	 */
	private static final String SERVER_FILE = "registerPurchase.php";

	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;
	/**
	 * The user that purchases items registered with this object.
	 */
	private final String mUser;
	/**
	 * The world the player price data of items registered with this object
	 * belongs to.
	 */
	private final EWorld mWorld;

	/**
	 * Creates a new purchase register that is able to register item purchases
	 * in a database.
	 * 
	 * @param user
	 *            The user that purchases items registered with this object
	 * @param world
	 *            The world the player price data of items registered with this
	 *            object belongs to
	 */
	public PurchaseRegister(final String user, final EWorld world) {
		this.mUser = user;
		this.mWorld = world;
		this.mLogger = LoggerFactory.getLogger();
	}

	/**
	 * Registers the purchase of the given item in a database.
	 * 
	 * @param item
	 *            The item whose purchase is to be registered
	 * @throws PurchaseRegisterServiceUnavailableException
	 *             When the service used to register the purchase is unavailable
	 *             such that a connection could not be established.
	 */
	public void registerPurchase(final Item item) throws PurchaseRegisterServiceUnavailableException {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Register purchase: " + item);
		}

		try {
			final URL url = new URL(StoreUtil.SERVER_URL + StoreUtil.BEEDLE_BOT_SERVICE + SERVER_FILE);
			final HashMap<String, String> arguments = new HashMap<>();
			final ItemPrice itemPrice = item.getStorePriceData();

			final String emptyText = "";

			// Put data
			arguments.put(ARG_ID, item.getId() + emptyText);
			arguments.put(ARG_USER, this.mUser);
			arguments.put(ARG_WORLD, StoreUtil.worldToNumber(this.mWorld) + emptyText);
			arguments.put(ARG_ITEM, item.getName());
			arguments.put(ARG_COST, item.getCost() + emptyText);
			arguments.put(ARG_PROFIT, item.getProfit() + emptyText);
			arguments.put(ARG_STANDARD_SHOP_PRICE, itemPrice.getStandardShopPrice() + emptyText);
			if (itemPrice.hasPlayerPrice()) {
				final PlayerPrice playerPrice = itemPrice.getPlayerPrice().get();
				arguments.put(ARG_PLAYER_PRICE, playerPrice.getPrice() + emptyText);
				arguments.put(ARG_TS_PLAYER_PRICE, StoreUtil.millisToSeconds(playerPrice.getTimestamp()) + emptyText);
			}
			arguments.put(ARG_IS_CONSIDERED_FOR_SHOP, item.isConsideredForShop() + emptyText);
			arguments.put(ARG_WAS_CACHED, itemPrice.isCached() + emptyText);
			arguments.put(ARG_TS_CACHE, StoreUtil.millisToSeconds(itemPrice.getLookupTimestamp()) + emptyText);

			try {
				StoreUtil.sendPostRequest(url, arguments);
			} catch (final IOException e) {
				throw new PurchaseRegisterServiceUnavailableException(e);
			}
		} catch (final MalformedURLException e) {
			throw new PurchaseRegisterServiceUnavailableException(e);
		}
	}
}
