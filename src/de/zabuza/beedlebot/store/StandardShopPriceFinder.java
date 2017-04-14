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

	private static String parseItem(final String itemName) {
		if (itemName.matches(".*Gewebeprobe.*")) {
			return "Gewebeprobe";
		}
		if (itemName.matches(".*Puppe.*")) {
			return "Puppe von Beispieluser";
		}
		if (itemName.matches(".*personalisierter Hinzauber.*")) {
			return "personalisierter Hinzauber";
		}
		if (itemName.matches(".*Zeichnung.*")) {
			return "Zeichnung von Beispiel-NPC";
		}
		if (itemName.matches(".*Blutprobe.*")) {
			return "Blutprobe";
		}
		if (itemName.matches(".*Seelenstein.*")) {
			return "Seelenstein von Beispielopfer";
		}
		if (itemName.matches(".*Wein.*")) {
			return "Wein von Beispielsponsor";
		}
		if (itemName.matches(".*Geschenk.*")) {
			return "Geschenk von Beispielsponsor";
		}
		if (itemName.matches(".*Schnaps.*")) {
			return "Schnaps von Beispielsponsor";
		}
		if (itemName.matches(".*Kaktussaft.*")) {
			return "Kaktussaft von Beispielsponsor";
		}
		if (itemName.matches(".*Largudsaft.*")) {
			return "Largudsaft von Beispielsponsor";
		}
		if (itemName.matches(".*Cocktail.*")) {
			return "Cocktail von Beispielsponsor";
		}
		if (itemName.matches(".*Tee.*")) {
			return "Tee von Beispielsponsor";
		}
		if (itemName.matches(".*Zaubertruhe von.*")) {
			return "Zaubertruhe von Beispieluser";
		}
		if (itemName.matches(".*Rückangriff.*")) {
			return "starker Rückangriffszauber";
		}
		if (itemName.matches(".*Tagebuch.*")) {
			return "Tagebuch Tag 125";
		}
		if (itemName.matches(".*Notizblock.*")) {
			return "Notizblock";
		}
		if (itemName.matches(".*Freundschaftsring.*")) {
			return "Freundschaftsring";
		}
		if (itemName.matches(".*Ehering.*")) {
			return "Ehering";
		}
		if (itemName.matches(".*Foliant.*")) {
			return "Foliant der Blutprobenwesen";
		}
		if (itemName.matches(".*Hirtenstab.*")) {
			return "Hirtenstab";
		}
		if (itemName.matches(".*Knorpel-Monster aus Draht.*")) {
			return "Knorpel-Monster aus Draht (Item)";
		}
		if (itemName.matches(".*Schatztruhe.*")) {
			return "Zaubertruhe";
		}
		if (itemName.matches(".*Sprengkapsel.*")) {
			return "Sumpfgasbombe";
		}
		if (itemName.matches(".*Knorpel-Monster aus Draht.*")) {
			return "Knorpel-Monster aus Draht (Item)";
		}
		return itemName;
	}

	public Optional<Integer> findStandardShopPrice(final String itemName) {
		Integer shopPrice = null;

		final String parsedItemName = parseItem(itemName);

		// Process exceptional items
		final Optional<Integer> exceptionalPrice = processExceptionalItems(itemName);
		if (exceptionalPrice.isPresent()) {
			return exceptionalPrice;
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
			// TODO Correct error handling and logging
			e.printStackTrace();
			return Optional.empty();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
					// TODO Correct error handling and logging
					e.printStackTrace();
					return Optional.empty();
				}
			}
		}

		return Optional.of(shopPrice);
	}

	private Optional<Integer> processExceptionalItems(final String itemName) {
		if (itemName.matches("altes Relikt")) {
			return Optional.of(0);
		}

		return Optional.empty();
	}
}
