package de.zabuza.beedlebot.store;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import de.zabuza.sparkle.freewar.EWorld;

public final class StoreCache {
	private final HashMap<String, ItemPrice> mNameToPriceData;
	private final PlayerPriceFinder mPlayerPriceFinder;
	private final StandardShopPriceFinder mStandardShopPriceFinder;
	private final EWorld mWorld;

	public StoreCache(final EWorld world) {
		// TODO Implement serialization
		mNameToPriceData = new HashMap<>();
		mWorld = world;
		mStandardShopPriceFinder = new StandardShopPriceFinder();
		mPlayerPriceFinder = new PlayerPriceFinder(world);
	}

	public void clear() {
		mNameToPriceData.clear();
	}

	public Collection<ItemPrice> getAllItemPrices() {
		return Collections.unmodifiableCollection(mNameToPriceData.values());
	}

	public ItemPrice getItemPrice(final String itemName) {
		return mNameToPriceData.get(itemName);
	}

	public boolean hasItemPrice(final String itemName) {
		return mNameToPriceData.containsKey(itemName);
	}

	public void putItemPrice(final ItemPrice itemPrice) {
		mNameToPriceData.put(itemPrice.getName(), itemPrice);
	}
}
