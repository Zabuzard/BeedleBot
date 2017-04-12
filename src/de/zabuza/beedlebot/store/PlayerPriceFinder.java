package de.zabuza.beedlebot.store;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import de.zabuza.sparkle.freewar.EWorld;

public final class PlayerPriceFinder {
	private static final String QUERY_PARAMETER_ITEM = "item";
	private static final String QUERY_PARAMETER_WORLD = "world";
	private static final String RESULT_KEY_PRICE = "price";
	private static final String RESULT_KEY_TIMESTAMP = "ts";
	private static final String SERVER_FILE = "itemPrice.php";

	public Optional<PlayerPrice> findPlayerPrice(final String itemName, final EWorld world) {
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
				// TODO Correct error handling and logging
				return Optional.empty();
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
			// TODO Correct error handling and logging
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
