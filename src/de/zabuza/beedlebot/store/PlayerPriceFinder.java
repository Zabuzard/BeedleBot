package de.zabuza.beedlebot.store;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import de.zabuza.beedlebot.exceptions.PlayerPriceServiceAnswerWrongFormatException;
import de.zabuza.beedlebot.exceptions.PlayerPriceServiceUnavailableException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.sparkle.freewar.EWorld;

/**
 * Service which finds player prices for given items. Use
 * {@link #findPlayerPrice(String, EWorld)} to access the service.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class PlayerPriceFinder {
	/**
	 * The query key for an item name value.
	 */
	private static final String QUERY_PARAMETER_ITEM = "item";
	/**
	 * The query key for a world value.
	 */
	private static final String QUERY_PARAMETER_WORLD = "world";
	/**
	 * The result key for a price value.
	 */
	private static final String RESULT_KEY_PRICE = "price";
	/**
	 * The result key for a timestamp value.
	 */
	private static final String RESULT_KEY_TIMESTAMP = "ts";
	/**
	 * The path to the file that offers the service on the server.
	 */
	private static final String SERVER_FILE = "itemPrice.php";

	/**
	 * The dictionary to use for exceptional items.
	 */
	private final ItemDictionary mItemDictionary;
	/**
	 * The logger to use for logging
	 */
	private final ILogger mLogger;

	/**
	 * Creates a new player price finder that is able to find player price data
	 * to a given item. Use {@link #findPlayerPrice(String, EWorld)} to access
	 * the service.
	 * 
	 * @param itemDictionary
	 *            The dictionary to use for exceptional items
	 */
	public PlayerPriceFinder(final ItemDictionary itemDictionary) {
		this.mItemDictionary = itemDictionary;
		this.mLogger = LoggerFactory.getLogger();
	}

	/**
	 * Tries to find player price data for the given item in the given world.
	 * 
	 * @param itemName
	 *            The name of the item to find its player price
	 * @param world
	 *            The world of where to find the player price data for
	 * @return If present the player price data for the given item in the given
	 *         world, if not present there is no
	 * @throws PlayerPriceServiceAnswerWrongFormatException
	 *             When the service used to fetch the player price data sends an
	 *             answer that is in the wrong format such that it could not be
	 *             parsed correctly
	 * @throws PlayerPriceServiceUnavailableException
	 *             When the service that is used to fetch player price data is
	 *             unavailable such that a connection could not be established
	 */
	public Optional<PlayerPrice> findPlayerPrice(final String itemName, final EWorld world)
			throws PlayerPriceServiceAnswerWrongFormatException, PlayerPriceServiceUnavailableException {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Finding player price: " + itemName + ", " + world);
		}

		// Process exceptional items
		if (this.mItemDictionary.containsPlayerPrice(itemName)) {
			final int price = this.mItemDictionary.getPlayerPrice(itemName).get().intValue();
			final long timestampNow = System.currentTimeMillis();
			return Optional.of(new PlayerPrice(price, timestampNow, world));
		}

		final int worldAsNumber = StoreUtil.worldToNumber(world);
		final String encodedItemName = StoreUtil.encodeUtf8(itemName);
		try {
			final StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append(StoreUtil.SERVER_URL);
			queryBuilder.append(StoreUtil.PLAYER_PRICE_SERVICE);
			queryBuilder.append(SERVER_FILE);
			queryBuilder.append(StoreUtil.QUERY_BEGIN);
			queryBuilder.append(QUERY_PARAMETER_WORLD);
			queryBuilder.append(StoreUtil.QUERY_ALLOCATION);
			queryBuilder.append(worldAsNumber);
			queryBuilder.append(StoreUtil.QUERY_SEPARATOR);
			queryBuilder.append(QUERY_PARAMETER_ITEM);
			queryBuilder.append(StoreUtil.QUERY_ALLOCATION);
			queryBuilder.append(encodedItemName);

			final URL url = new URL(queryBuilder.toString());
			final JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(url.openStream()));

			if (!parser.hasNext()) {
				throw new PlayerPriceServiceAnswerWrongFormatException(itemName, world);
			}

			final JsonObject element = parser.next().getAsJsonObject();

			// No price known
			if (!element.has(RESULT_KEY_PRICE)) {
				return Optional.empty();
			}

			final int price = element.get(RESULT_KEY_PRICE).getAsInt();
			final long timestamp = StoreUtil.secondsToMillis(element.get(RESULT_KEY_TIMESTAMP).getAsLong());

			final PlayerPrice playerPrice = new PlayerPrice(price, timestamp, world);
			return Optional.of(playerPrice);
		} catch (final IOException e) {
			throw new PlayerPriceServiceUnavailableException(e);
		}
	}
}
