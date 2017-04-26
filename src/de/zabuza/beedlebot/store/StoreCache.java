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

import de.zabuza.beedlebot.exceptions.StoreCacheDeserializationUnsuccessfulException;
import de.zabuza.beedlebot.exceptions.StoreCacheSerializationUnsuccessfulException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.sparkle.freewar.EWorld;

/**
 * Cache that stores item price data for a {@link Store}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class StoreCache implements Serializable {
	/**
	 * Prefix of the file-path to a serialized cache.
	 */
	private static final String FILEPATH_SERIALIZATION_PRE = "storeCache_";
	/**
	 * Suffix of the file-path to a serialized cache.
	 */
	private static final String FILEPATH_SERIALIZATION_SUFF = ".ser";
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Deserializes the cache for the given world. Before attempting to call
	 * this method ensure there is a cache with
	 * {@link #hasSerializedCache(EWorld)}. Serialization can be done with
	 * {@link #serialize()}.
	 * 
	 * @param world
	 *            The world of the cache to deserialize
	 * @return The deserialized instance of the cache for the given world
	 * @throws StoreCacheDeserializationUnsuccessfulException
	 *             If the deserialization of the cache was unsuccessful
	 */
	public static StoreCache deserialize(final EWorld world) throws StoreCacheDeserializationUnsuccessfulException {
		LoggerFactory.getLogger().logInfo("Deserializing StoreCache");

		StoreCache cache = null;
		try (final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(buildSerializationPath(world)))) {
			cache = (StoreCache) ois.readObject();
			cache.refreshLogger();
		} catch (final IOException | ClassNotFoundException e) {
			throw new StoreCacheDeserializationUnsuccessfulException(e);
		}
		return cache;
	}

	/**
	 * Whether there is a serialized cache for the given world that can be
	 * deserialized or not. Use {@link #serialize()} for serialization and
	 * {@link #deserialize(EWorld)} for deserialization.
	 * 
	 * @param world
	 *            The world of the cache
	 * @return <tt>True</tt> if there is a serialized cache for the given world,
	 *         <tt>false</tt> if not
	 */
	public static boolean hasSerializedCache(final EWorld world) {
		final File cacheFile = new File(buildSerializationPath(world));
		return cacheFile.exists() && !cacheFile.isDirectory();
	}

	/**
	 * Builds the path to the serialized cache for the given world.
	 * 
	 * @param world
	 *            The world of the cache
	 * @return The path to the serialized cache for the given world
	 */
	private static String buildSerializationPath(final EWorld world) {
		return FILEPATH_SERIALIZATION_PRE + world + FILEPATH_SERIALIZATION_SUFF;
	}

	/**
	 * The logger to use for logging which is not contained in serialized
	 * objects, instead use {@link #refreshLogger()} to initialize the logger if
	 * deserializing a store cache..
	 */
	private transient ILogger mLogger;

	/**
	 * Data-structure that maps item names to their cached price data.
	 */
	private final HashMap<String, ItemPrice> mNameToPriceData;
	/**
	 * The world the price data of the cache belongs to.
	 */
	private final EWorld mWorld;

	/**
	 * Creates a new empty store cache for the given world which can cache item
	 * price data.
	 * 
	 * @param world
	 *            The world the price data of the cache belongs to
	 */
	public StoreCache(final EWorld world) {
		this.mNameToPriceData = new HashMap<>();
		this.mWorld = world;
		this.mLogger = LoggerFactory.getLogger();
	}

	/**
	 * Clears the cache, i.e. removing all item price data from it.
	 */
	public void clear() {
		this.mNameToPriceData.clear();
	}

	/**
	 * Gets an unmodifiable collection of all item price data this cache has
	 * stored.
	 * 
	 * @return An unmodifiable collection of all item price data this cache has
	 *         stored
	 */
	public Collection<ItemPrice> getAllItemPrices() {
		return Collections.unmodifiableCollection(this.mNameToPriceData.values());
	}

	/**
	 * Gets the item price data for the item with the given name from the cache.
	 * 
	 * @param itemName
	 *            The name of the item to get its price
	 * @return The item price data for the item with the given name
	 */
	public ItemPrice getItemPrice(final String itemName) {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Get item price from cache: " + itemName);
		}
		return this.mNameToPriceData.get(itemName);
	}

	/**
	 * Whether the cache has stored item price data for the item with the given
	 * name or not.
	 * 
	 * @param itemName
	 *            The name of the item in question
	 * @return <tt>True</tt> if the cache has stored item price data for the
	 *         item with the given name, <tt>false</tt> if not
	 */
	public boolean hasItemPrice(final String itemName) {
		return this.mNameToPriceData.containsKey(itemName);
	}

	/**
	 * Stores the given item price data in the cache.
	 * 
	 * @param itemPrice
	 *            The item price data to store in the cache
	 */
	public void putItemPrice(final ItemPrice itemPrice) {
		this.mNameToPriceData.put(itemPrice.getName(), itemPrice);
	}

	/**
	 * Serializes this store. Afterwards instances of this store can be created
	 * by using {@link #hasSerializedCache(EWorld)} and
	 * {@link #deserialize(EWorld)}.
	 * 
	 * @throws StoreCacheSerializationUnsuccessfulException
	 *             If the serialization of the cache was unsuccessful
	 */
	public void serialize() throws StoreCacheSerializationUnsuccessfulException {
		this.mLogger.logInfo("Serializing StoreCache");

		try (final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(buildSerializationPath(this.mWorld)))) {
			oos.writeObject(this);
		} catch (final IOException e) {
			throw new StoreCacheSerializationUnsuccessfulException(e);
		}
	}

	/**
	 * Gets the size of this cache, i.e. the amount of item price data stored in
	 * this cache.
	 * 
	 * @return The size of this cache, i.e. the amount of item price data stored
	 *         in this cache
	 */
	public int size() {
		return this.mNameToPriceData.size();
	}

	/**
	 * Refreshes the internal reference to the logger.
	 */
	private void refreshLogger() {
		this.mLogger = LoggerFactory.getLogger();
	}
}
