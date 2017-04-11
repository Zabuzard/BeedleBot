package de.zabuza.beedlebot.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import de.zabuza.sparkle.freewar.EWorld;

public final class StoreCache implements Serializable {
	private static final String FILEPATH_SERIALIZATION_PRE = "storeCache_";

	private static final String FILEPATH_SERIALIZATION_SUC = ".ser";
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public static StoreCache deserialize(final EWorld world) {
		StoreCache cache = null;
		ObjectInputStream ois = null;
		try {
			final FileInputStream fis = new FileInputStream(buildSerializationPath(world));
			ois = new ObjectInputStream(fis);
			cache = (StoreCache) ois.readObject();
		} catch (final IOException | ClassNotFoundException e) {
			// TODO Correct error handling and logging
			System.err.println("Error while deserializing store cache.");
			System.err.println(e);
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (final IOException e) {
				// TODO Correct error handling and logging
				System.err.println("Error while closing deserialization stream of store cache.");
				System.err.println(e);
			}
		}
		return cache;
	}

	public static boolean hasSerializedCache(final EWorld world) {
		final File cacheFile = new File(buildSerializationPath(world));
		return cacheFile.exists() && !cacheFile.isDirectory();
	}

	private static String buildSerializationPath(final EWorld world) {
		return FILEPATH_SERIALIZATION_PRE + world + FILEPATH_SERIALIZATION_SUC;
	}

	private final HashMap<String, ItemPrice> mNameToPriceData;

	private final EWorld mWorld;

	public StoreCache(final EWorld world) {
		// TODO Implement serialization
		mNameToPriceData = new HashMap<>();
		mWorld = world;
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

	public void serialize() {
		ObjectOutputStream oos = null;
		try {
			final FileOutputStream fos = new FileOutputStream(buildSerializationPath(mWorld));
			oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
		} catch (final IOException e) {
			// TODO Correct error handling and logging
			System.err.println("Error while serializing store cache.");
			System.err.println(e);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (final IOException e) {
				// TODO Correct error handling and logging
				System.err.println("Error while closing serialization stream of store cache.");
				System.err.println(e);
			}
		}
	}
}
