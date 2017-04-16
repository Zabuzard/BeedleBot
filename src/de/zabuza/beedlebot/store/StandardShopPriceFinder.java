package de.zabuza.beedlebot.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

public final class StandardShopPriceFinder {

	public static final int NO_PRICE = -1;
	private static final String CONTENT_END_PATTERN_FIRST = "|";
	private static final String CONTENT_END_PATTERN_SECOND = "}";
	private static final String CONTENT_START_PATTERN = "|VerkPreis=";
	private static final String SERVER_QUERY_POST = "&action=edit";
	private static final String SERVER_QUERY_PRE = "?title=";
	private static final String SERVER_URL = "http://www.fwwiki.de/index.php";
	private static final String STRIP_INTEGER_PATTERN = "[\\s\\.,]";

	private final ItemDictionary mItemDictionary;

	public StandardShopPriceFinder(final ItemDictionary itemDictionary) {
		mItemDictionary = itemDictionary;
	}

	public Optional<Integer> findStandardShopPrice(final String itemName) {
		Integer shopPrice = null;

		final String parsedItemName = mItemDictionary.applyItemNamePatterns(itemName);

		// Process exceptional items
		if (mItemDictionary.containsStandardShopPrice(parsedItemName)) {
			return mItemDictionary.getStandardShopPrice(parsedItemName);
		}

		BufferedReader br = null;
		try {
			final String itemToUrl = parsedItemName.replaceAll("\\s", "_");
			final URL url = new URL(SERVER_URL + SERVER_QUERY_PRE + itemToUrl + SERVER_QUERY_POST);
			br = new BufferedReader(new InputStreamReader(url.openStream()));

			// Find shop price parameter
			final StringBuilder shopPriceContent = new StringBuilder();
			int startIndex = -1;
			while (br.ready()) {
				final String line = br.readLine();
				if (line == null) {
					break;
				}
				startIndex = line.indexOf(CONTENT_START_PATTERN);

				if (startIndex != -1) {
					// Add rest of line
					shopPriceContent.append(line.substring(startIndex + CONTENT_START_PATTERN.length()));
					break;
				}
			}
			if (startIndex == -1) {
				return Optional.empty();
			}

			// Find shop price parameter ending
			int endIndex = -1;
			while (br.ready()) {
				final String line = br.readLine();
				if (line == null) {
					break;
				}
				endIndex = line.indexOf(CONTENT_END_PATTERN_FIRST);
				if (endIndex == -1) {
					// Try next pattern
					endIndex = line.indexOf(CONTENT_END_PATTERN_SECOND);
				}

				if (endIndex != -1) {
					// Add beginning of line
					shopPriceContent.append(line.substring(0, endIndex));
					break;
				} else {
					// Add whole line
					shopPriceContent.append(line);
				}
			}
			if (endIndex == -1) {
				return Optional.empty();
			}

			// Price extracted, format it
			final String shopPriceText = shopPriceContent.toString();
			shopPrice = Integer.parseInt(shopPriceText.replaceAll(STRIP_INTEGER_PATTERN, ""));
		} catch (final IOException e) {
			// TODO Exchange with a more specific exception
			throw new IllegalStateException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
					// TODO Exchange with a more specific exception
					throw new IllegalStateException(e);
				}
			}
		}

		return Optional.of(shopPrice);
	}
}
