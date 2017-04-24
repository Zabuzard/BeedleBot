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
		this.mStandardShopPrices = new HashMap<>();
		this.mPlayerPrices = new HashMap<>();
		this.mItemNamePatterns = new HashMap<>();
		this.mItemsRegisteredForShop = new HashSet<>();
		this.mItemsRegisteredForPlayer = new HashSet<>();

		initializeStandardShopPrices();
		initializePlayerPrices();

		initializeItemsRegisteredForShop();
		initializeItemsRegisteredForPlayer();

		initializeItemNamePatterns();
	}

	public String applyItemNamePatterns(final String itemName) {
		for (final Entry<String, String> entry : this.mItemNamePatterns.entrySet()) {
			if (itemName.matches(entry.getKey())) {
				return entry.getValue();
			}
		}
		return itemName;
	}

	public boolean containsPlayerPrice(final String itemName) {
		return this.mPlayerPrices.containsKey(itemName);
	}

	public boolean containsStandardShopPrice(final String itemName) {
		return this.mStandardShopPrices.containsKey(itemName);
	}

	public Optional<Integer> getPlayerPrice(final String itemName) {
		if (containsPlayerPrice(itemName)) {
			return Optional.of(this.mPlayerPrices.get(itemName));
		}
		return Optional.empty();
	}

	public Optional<Integer> getStandardShopPrice(final String itemName) {
		if (containsStandardShopPrice(itemName)) {
			return Optional.of(this.mStandardShopPrices.get(itemName));
		}
		return Optional.empty();
	}

	public boolean isItemRegisteredForPlayer(final String itemName) {
		return this.mItemsRegisteredForPlayer.contains(itemName);
	}

	public boolean isItemRegisteredForShop(final String itemName) {
		// Consider every item that is not explicitly registered as player item
		// as shop item as they are to hard to sell to players
		if (!isItemRegisteredForPlayer(itemName)) {
			return true;
		}

		return this.mItemsRegisteredForShop.contains(itemName);
	}

	private void initializeItemNamePatterns() {
		this.mItemNamePatterns.put(".*Gewebeprobe.", "Gewebeprobe");
		this.mItemNamePatterns.put(".*Puppe.*", "Puppe von Beispieluser");
		this.mItemNamePatterns.put(".*personalisierter Hinzauber.*", "personalisierter Hinzauber");
		this.mItemNamePatterns.put(".*Zeichnung.*", "Zeichnung von Beispiel-NPC");
		this.mItemNamePatterns.put(".*Blutprobe.*", "Blutprobe");
		this.mItemNamePatterns.put(".*Seelenstein.*", "Seelenstein von Beispielopfer");
		this.mItemNamePatterns.put(".*Wein.*", "Wein von Beispielsponsor");
		this.mItemNamePatterns.put(".*Geschenk.*", "Geschenk von Beispielsponsor");
		this.mItemNamePatterns.put(".*Schnaps.*", "Schnaps von Beispielsponsor");
		this.mItemNamePatterns.put(".*Kaktussaft.*", "Kaktussaft von Beispielsponsor");
		this.mItemNamePatterns.put(".*Largudsaft.*", "Largudsaft von Beispielsponsor");
		this.mItemNamePatterns.put(".*Cocktail.*", "Cocktail von Beispielsponsor");
		this.mItemNamePatterns.put(".*Tee.*", "Tee von Beispielsponsor");
		this.mItemNamePatterns.put(".*Zaubertruhe von.*", "Zaubertruhe von Beispieluser");
		this.mItemNamePatterns.put(".*Rückangriff.*", "starker Rückangriffszauber");
		this.mItemNamePatterns.put(".*Tagebuch.*", "Tagebuch Tag 125");
		this.mItemNamePatterns.put(".*Notizblock.*", "Notizblock");
		this.mItemNamePatterns.put(".*Freundschaftsring.*", "Freundschaftsring");
		this.mItemNamePatterns.put(".*Ehering.*", "Ehering");
		this.mItemNamePatterns.put(".*Foliant.*", "Foliant der Blutprobenwesen");
		this.mItemNamePatterns.put(".*Hirtenstab.*", "Hirtenstab");
		this.mItemNamePatterns.put(".*Knorpel-Monster aus Draht.*", "Knorpel-Monster aus Draht (Item)");
		this.mItemNamePatterns.put(".*Schatztruhe.*", "Zaubertruhe");
		this.mItemNamePatterns.put(".*Sprengkapsel.*", "Sumpfgasbombe");
		this.mItemNamePatterns.put(".*Wissenszauber von .*", "Wissenszauber von Beispieluser");
		this.mItemNamePatterns.put(".*Hinzauber zu .*", "Hinzauber zu Beispielspieler");
		this.mItemNamePatterns.put(".*Forschungssalz der .*", "Forschungssalz der XY-Mutation");
	}

	private void initializeItemsRegisteredForPlayer() {
		this.mItemsRegisteredForPlayer.add("Wakrudpilz");
		this.mItemsRegisteredForPlayer.add("Kuhkopf");
		this.mItemsRegisteredForPlayer.add("Seelenkapsel");
		this.mItemsRegisteredForPlayer.add("Seelenkugel");
		this.mItemsRegisteredForPlayer.add("toter Blutwurm");
		this.mItemsRegisteredForPlayer.add("Onlo-Knochen");
		this.mItemsRegisteredForPlayer.add("tote Wüstenmaus");
		this.mItemsRegisteredForPlayer.add("Pfeil");
		this.mItemsRegisteredForPlayer.add("Phasenkugel");
		this.mItemsRegisteredForPlayer.add("Zauberbrötchen");
		this.mItemsRegisteredForPlayer.add("Holz");
		this.mItemsRegisteredForPlayer.add("Ölfass");
		this.mItemsRegisteredForPlayer.add("Artefakt von Dranar");
		this.mItemsRegisteredForPlayer.add("blauer Kristall");
		this.mItemsRegisteredForPlayer.add("roter Bergstein");
	}

	private void initializeItemsRegisteredForShop() {
		// Nothing there at the moment
	}

	private void initializePlayerPrices() {
		// Nothing there at the moment
	}

	private void initializeStandardShopPrices() {
		this.mStandardShopPrices.put("altes Relikt", Integer.valueOf(0));
	}
}
