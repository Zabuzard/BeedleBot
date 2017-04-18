package de.zabuza.beedlebot.store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

public final class ItemDictionary {

	private final Map<String, String> mItemNamePatterns;
	private final Set<String> mItemsRegisteredForPlayer;
	private final Set<String> mItemsRegisteredForShop;
	private final Map<String, Integer> mPlayerPrices;
	private final Map<String, Integer> mStandardShopPrices;

	public ItemDictionary() {
		mStandardShopPrices = new HashMap<>();
		mPlayerPrices = new HashMap<>();
		mItemNamePatterns = new HashMap<>();
		mItemsRegisteredForShop = new HashSet<>();
		mItemsRegisteredForPlayer = new HashSet<>();

		initializeStandardShopPrices();
		initializePlayerPrices();

		initializeItemsRegisteredForShop();
		initializeItemsRegisteredForPlayer();

		initializeItemNamePatterns();
	}

	public String applyItemNamePatterns(final String itemName) {
		for (final Entry<String, String> entry : mItemNamePatterns.entrySet()) {
			if (itemName.matches(entry.getKey())) {
				return entry.getValue();
			}
		}
		return itemName;
	}

	public boolean containsPlayerPrice(final String itemName) {
		return mPlayerPrices.containsKey(itemName);
	}

	public boolean containsStandardShopPrice(final String itemName) {
		return mStandardShopPrices.containsKey(itemName);
	}

	public Optional<Integer> getPlayerPrice(final String itemName) {
		if (containsPlayerPrice(itemName)) {
			return Optional.of(mPlayerPrices.get(itemName));
		} else {
			return Optional.empty();
		}
	}

	public Optional<Integer> getStandardShopPrice(final String itemName) {
		if (containsStandardShopPrice(itemName)) {
			return Optional.of(mStandardShopPrices.get(itemName));
		} else {
			return Optional.empty();
		}
	}

	public boolean isItemRegisteredForPlayer(final String itemName) {
		return mItemsRegisteredForPlayer.contains(itemName);
	}

	public boolean isItemRegisteredForShop(final String itemName) {
		return mItemsRegisteredForShop.contains(itemName);
	}

	private void initializeItemNamePatterns() {
		mItemNamePatterns.put(".*Gewebeprobe.", "Gewebeprobe");
		mItemNamePatterns.put(".*Puppe.*", "Puppe von Beispieluser");
		mItemNamePatterns.put(".*personalisierter Hinzauber.*", "personalisierter Hinzauber");
		mItemNamePatterns.put(".*Zeichnung.*", "Zeichnung von Beispiel-NPC");
		mItemNamePatterns.put(".*Blutprobe.*", "Blutprobe");
		mItemNamePatterns.put(".*Seelenstein.*", "Seelenstein von Beispielopfer");
		mItemNamePatterns.put(".*Wein.*", "Wein von Beispielsponsor");
		mItemNamePatterns.put(".*Geschenk.*", "Geschenk von Beispielsponsor");
		mItemNamePatterns.put(".*Schnaps.*", "Schnaps von Beispielsponsor");
		mItemNamePatterns.put(".*Kaktussaft.*", "Kaktussaft von Beispielsponsor");
		mItemNamePatterns.put(".*Largudsaft.*", "Largudsaft von Beispielsponsor");
		mItemNamePatterns.put(".*Cocktail.*", "Cocktail von Beispielsponsor");
		mItemNamePatterns.put(".*Tee.*", "Tee von Beispielsponsor");
		mItemNamePatterns.put(".*Zaubertruhe von.*", "Zaubertruhe von Beispieluser");
		mItemNamePatterns.put(".*Rückangriff.*", "starker Rückangriffszauber");
		mItemNamePatterns.put(".*Tagebuch.*", "Tagebuch Tag 125");
		mItemNamePatterns.put(".*Notizblock.*", "Notizblock");
		mItemNamePatterns.put(".*Freundschaftsring.*", "Freundschaftsring");
		mItemNamePatterns.put(".*Ehering.*", "Ehering");
		mItemNamePatterns.put(".*Foliant.*", "Foliant der Blutprobenwesen");
		mItemNamePatterns.put(".*Hirtenstab.*", "Hirtenstab");
		mItemNamePatterns.put(".*Knorpel-Monster aus Draht.*", "Knorpel-Monster aus Draht (Item)");
		mItemNamePatterns.put(".*Schatztruhe.*", "Zaubertruhe");
		mItemNamePatterns.put(".*Sprengkapsel.*", "Sumpfgasbombe");
	}

	private void initializeItemsRegisteredForPlayer() {
		mItemsRegisteredForPlayer.add("Wakrudpilz");
		mItemsRegisteredForPlayer.add("Kuhkopf");
		mItemsRegisteredForPlayer.add("Seelenkapsel");
		mItemsRegisteredForPlayer.add("Seelenkugel");
		mItemsRegisteredForPlayer.add("toter Blutwurm");
		mItemsRegisteredForPlayer.add("Onlo-Knochen");
		mItemsRegisteredForPlayer.add("tote Wüstenmaus");
		mItemsRegisteredForPlayer.add("Pfeil");
		mItemsRegisteredForPlayer.add("Phasenkugel");
		mItemsRegisteredForPlayer.add("Zauberbrötchen");
		mItemsRegisteredForPlayer.add("Holz");
		mItemsRegisteredForPlayer.add("Ölfass");
		mItemsRegisteredForPlayer.add("Artefakt von Dranar");
		mItemsRegisteredForPlayer.add("blauer Kristall");
	}

	private void initializeItemsRegisteredForShop() {

	}

	private void initializePlayerPrices() {

	}

	private void initializeStandardShopPrices() {
		mStandardShopPrices.put("altes Relikt", 0);
		mStandardShopPrices.put("Finstereis-Bewacher", 0);
	}
}
