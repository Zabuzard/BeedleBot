package de.zabuza.beedlebot.databridge;

/**
 * Utility class that provides mappings for the keys used in the storage
 * communication channel of the {@link DataBridge}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class StorageKeys {
	/**
	 * The storage key of the current lifepoints value.
	 */
	public final static String CUR_LIFEPOINTS = "curLife";
	/**
	 * The storage key of the gold value.
	 */
	public final static String GOLD = "gold";
	/**
	 * The storage key of the heart-beat value.
	 */
	public final static String HEART_BEAT = "heartBeat";
	/**
	 * The storage key of the inventory size value.
	 */
	public final static String INVENTORY_SIZE = "invSize";
	/**
	 * The storage key of the active flag.
	 */
	public final static String IS_ACTIVE = "isActive";
	/**
	 * The storage key of the tool serving flag.
	 */
	public final static String IS_BEEDLE_BOT_SERVING = "isBeedleBotServing";
	/**
	 * The storage key of the item entries value.
	 */
	public final static String ITEM_ENTRIES = "itemEntries";
	/**
	 * The index of the storage keys which is used as prefix for all keys.
	 */
	public final static String KEY_INDEX = "beedle_";
	/**
	 * The storage key of the maximal inventory size value.
	 */
	public final static String MAX_INVENTORY_SIZE = "maxInvSize";
	/**
	 * The storage key of the maximal lifepoints value.
	 */
	public final static String MAX_LIFEPOINTS = "maxLife";
	/**
	 * The storage key of the phase value.
	 */
	public final static String PHASE = "phase";
	/**
	 * The storage key of the problem value.
	 */
	public final static String PROBLEM = "problem";
	/**
	 * The storage key of the start signal flag.
	 */
	public final static String START_SIGNAL = "startSignal";
	/**
	 * The storage key of the state value.
	 */
	public final static String STATE = "state";
	/**
	 * The storage key of the stop signal flag.
	 */
	public final static String STOP_SIGNAL = "stopSignal";
	/**
	 * The storage key of the total cost value.
	 */
	public final static String TOTAL_COST = "totalCost";
	/**
	 * The storage key of the total profit value.
	 */
	public final static String TOTAL_PROFIT = "totalProfit";
	/**
	 * The storage key of the waiting time value.
	 */
	public final static String WAITING_TIME = "waitingTime";

	/**
	 * Utility class. No Implementation.
	 */
	private StorageKeys() {

	}

}
