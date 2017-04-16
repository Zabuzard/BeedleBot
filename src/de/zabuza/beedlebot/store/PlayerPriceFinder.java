package de.zabuza.beedlebot.store;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import de.zabuza.beedlebot.exceptions.PlayerPriceServiceAnswerWrongFormatException;
import de.zabuza.beedlebot.exceptions.PlayerPriceServiceUnavailableException;
import de.zabuza.sparkle.freewar.EWorld;

public final class PlayerPriceFinder {
	private static final String QUERY_PARAMETER_ITEM = "item";
	private static final String QUERY_PARAMETER_WORLD = "world";
	private static final String RESULT_KEY_PRICE = "price";
	private static final String RESULT_KEY_TIMESTAMP = "ts";
	private static final String SERVER_FILE = "itemPrice.php";

	private final ItemDictionary mItemDictionary;

	public PlayerPriceFinder(final ItemDictionary itemDictionary) {
		mItemDictionary = itemDictionary;
	}

	public Optional<PlayerPrice> findPlayerPrice(final String itemName, final EWorld world)
			throws PlayerPriceServiceAnswerWrongFormatException, PlayerPriceServiceUnavailableException {
		// Process exceptional items
		if (mItemDictionary.containsPlayerPrice(itemName)) {
			final int price = mItemDictionary.getPlayerPrice(itemName).get();
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
