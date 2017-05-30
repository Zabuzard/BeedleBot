package de.zabuza.beedlebot.store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * Provides various utility methods for item price data. Stores item price data
 * for exceptional items.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ItemDictionary {
	/**
	 * Data-structure which maps item name patterns for non-unique items to
	 * their unique representations.
	 */
	private final Map<String, String> mItemNamePatterns;
	/**
	 * Set which contains items that are explicitly registered to be sold to
	 * players.
	 */
	private final Set<String> mItemsRegisteredForPlayer;
	/**
	 * Set which contains items that are explicitly registered to be sold to the
	 * shop.
	 */
	private final Set<String> mItemsRegisteredForShop;
	/**
	 * Data-structure which maps item names to explicit player prices.
	 */
	private final Map<String, Integer> mPlayerPrices;
	/**
	 * Data-structure which maps item names to explicit standard shop prices.
	 */
	private final Map<String, Integer> mStandardShopPrices;

	/**
	 * Creates a new item dictionary.
	 */
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

	/**
	 * Applies known item name patterns to the given item name in order to get a
	 * unique representation of it in case it is a non unique item.
	 * 
	 * @param itemName
	 *            The name of the item to apply the patterns to
	 * @return A unique representation name of the given item
	 */
	public String applyItemNamePatterns(final String itemName) {
		for (final Entry<String, String> entry : this.mItemNamePatterns.entrySet()) {
			if (itemName.matches(entry.getKey())) {
				return entry.getValue();
			}
		}
		return itemName;
	}

	/**
	 * Whether the dictionary contains an explicit player price for the given
	 * item.
	 * 
	 * @param itemName
	 *            The name of the item in question
	 * @return <tt>True</tt> if the dictionary contains an explicit player price
	 *         for the given item, <tt>false</tt> if not
	 */
	public boolean containsPlayerPrice(final String itemName) {
		return this.mPlayerPrices.containsKey(itemName);
	}

	/**
	 * Whether the dictionary contains an explicit standard shop price for the
	 * given item.
	 * 
	 * @param itemName
	 *            The name of the item in question
	 * @return <tt>True</tt> if the dictionary contains an explicit standard
	 *         shop price for the given item, <tt>false</tt> if not
	 */
	public boolean containsStandardShopPrice(final String itemName) {
		return this.mStandardShopPrices.containsKey(itemName);
	}

	/**
	 * If present gets the explicit player price stored in this dictionary for
	 * the given item.
	 * 
	 * @param itemName
	 *            The name of the item to get its price
	 * @return If present the explicit player price stored in this dictionary
	 *         for the given item
	 */
	public Optional<Integer> getPlayerPrice(final String itemName) {
		if (containsPlayerPrice(itemName)) {
			return Optional.of(this.mPlayerPrices.get(itemName));
		}
		return Optional.empty();
	}

	/**
	 * If present gets the explicit standard shop price stored in this
	 * dictionary for the given item.
	 * 
	 * @param itemName
	 *            The name of the item to get its price
	 * @return If present the explicit standard shop price stored in this
	 *         dictionary for the given item
	 */
	public Optional<Integer> getStandardShopPrice(final String itemName) {
		if (containsStandardShopPrice(itemName)) {
			return Optional.of(this.mStandardShopPrices.get(itemName));
		}
		return Optional.empty();
	}

	/**
	 * Whether the given item is explicitly registered to be sold to players in
	 * this dictionary.
	 * 
	 * @param itemName
	 *            The name of the item in question
	 * @return <tt>True</tt> if the given item is explicitly registered to be
	 *         sold to players in this dictionary, <tt>false</tt> if not
	 */
	public boolean isItemRegisteredForPlayer(final String itemName) {
		return this.mItemsRegisteredForPlayer.contains(itemName);
	}

	/**
	 * Whether the given item is explicitly registered to be sold to the shop in
	 * this dictionary.
	 * 
	 * @param itemName
	 *            The name of the item in question
	 * @return <tt>True</tt> if the given item is explicitly registered to be
	 *         sold to the shop in this dictionary, <tt>false</tt> if not
	 */
	public boolean isItemRegisteredForShop(final String itemName) {
		// Consider every item that is not explicitly registered as player item
		// as shop item as they are to hard to sell to players
		if (!isItemRegisteredForPlayer(itemName)) {
			return true;
		}

		return this.mItemsRegisteredForShop.contains(itemName);
	}

	/**
	 * Initializes the data-structure that maps item name patterns with their
	 * unique representation name.
	 */
	private void initializeItemNamePatterns() {
		this.mItemNamePatterns.put(".*Gewebeprobe.*", "Gewebeprobe");
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

		// Dummy item used for items that are inexistent
		this.mItemNamePatterns.put(".*Geist von .*", "rostiger Werkzeugkoffer");
	}

	/**
	 * Initializes the data-structure that holds all items that are explicitly
	 * registered to be sold to players.
	 */
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

	/**
	 * Initializes the data-structure that holds all items that are explicitly
	 * registered to be sold to the shop.
	 */
	private void initializeItemsRegisteredForShop() {
		// Nothing there at the moment
	}

	/**
	 * Initializes the data-structure that maps item names to explicit player
	 * prices.
	 */
	private void initializePlayerPrices() {
		// Nothing there at the moment
	}

	/**
	 * Intializes the data-structure that maps item names to explicit standard
	 * shop prices.
	 */
	private void initializeStandardShopPrices() {
		this.mStandardShopPrices.put("altes Relikt", Integer.valueOf(0));
	}
}
