package de.zabuza.beedlebot.store;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import de.zabuza.sparkle.freewar.EWorld;

public final class PlayerPriceFinder {
	private static final String QUERY_ALLOCATION = "=";
	private static final String QUERY_BEGIN = "?";
	private static final String QUERY_PARAMETER_ITEM = "item";
	private static final String QUERY_PARAMETER_WORLD = "world";
	private static final String QUERY_SEPARATOR = "&";
	private static final String RESULT_KEY_PRICE = "price";
	private static final String RESULT_KEY_TIMESTAMP = "ts";
	private static final String SERVER_URL = "http://www.zabuza.square7.ch/freewar/mplogger/itemPrice.php";

	private static int worldToNumber(final EWorld world) {
		if (world == EWorld.ONE) {
			return 1;
		}
		if (world == EWorld.TWO) {
			return 2;
		}
		if (world == EWorld.THREE) {
			return 3;
		}
		if (world == EWorld.FOUR) {
			return 4;
		}
		if (world == EWorld.FIVE) {
			return 5;
		}
		if (world == EWorld.SIX) {
			return 6;
		}
		if (world == EWorld.SEVEN) {
			return 7;
		}
		if (world == EWorld.EIGHT) {
			return 8;
		}
		if (world == EWorld.NINE) {
			return 9;
		}
		if (world == EWorld.TEN) {
			return 10;
		}
		if (world == EWorld.ELEVEN) {
			return 11;
		}
		if (world == EWorld.TWELVE) {
			return 12;
		}
		if (world == EWorld.THIRTEEN) {
			return 13;
		}
		if (world == EWorld.FOURTEEN) {
			return 14;
		}

		// TODO Correct error handling and logging
		throw new IllegalArgumentException();
	}

	public Optional<PlayerPrice> findPlayerPrice(final String itemName, final EWorld world) {
		final int worldAsNumber = worldToNumber(world);
		try {
			final String encodedItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8.toString());
			final String query = SERVER_URL + QUERY_BEGIN + QUERY_PARAMETER_WORLD + QUERY_ALLOCATION + worldAsNumber
					+ QUERY_SEPARATOR + QUERY_PARAMETER_ITEM + QUERY_ALLOCATION + encodedItemName;
			final URL url = new URL(query);
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
			final long timestamp = element.get(RESULT_KEY_TIMESTAMP).getAsLong();

			final PlayerPrice playerPrice = new PlayerPrice(price, timestamp, world);
			return Optional.of(playerPrice);
		} catch (IOException e) {
			// TODO Correct error handling and logging
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
