package de.zabuza.beedlebot.databridge;

import java.text.DateFormat;
import java.util.Date;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.html5.WebStorage;

import de.zabuza.beedlebot.exceptions.DriverStorageUnsupportedException;
import de.zabuza.beedlebot.logging.LoggerUtil;
import de.zabuza.sparkle.webdriver.IWrapsWebDriver;

/**
 * The data bridge enables communication between the tool and its web interface
 * by using <a href="https://www.w3schools.com/html/html5_webstorage.asp">HTML 5
 * Webstorage technology</a> which acts as shared memory.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class DataBridge {

	/**
	 * The pattern which separates item entries in the item entry raw format.
	 */
	private static final String ITEM_ENTRY_SEPARATOR = ";";
	/**
	 * The pattern which separates values of an item entry in the item entry raw
	 * format.
	 */
	private static final String ITEM_VALUE_SEPARATOR = "?";

	/**
	 * Given a key it builds the actual key used inside the storage.
	 * 
	 * @param key
	 *            The key of the tool that should be converted to a storage key
	 * @return The actual key used inside the storage which is represented by
	 *         the given tool key
	 */
	private static String buildKey(final String key) {
		return StorageKeys.KEY_INDEX + key;
	}

	/**
	 * The storage to use for communication over the
	 * <a href="https://www.w3schools.com/html/html5_webstorage.asp">HTML 5
	 * Webstorage technology</a>.
	 */
	private final SessionStorage mStorage;

	/**
	 * Creates a new instance of a data bridge which is capable of communicating
	 * via the given drivers
	 * <a href="https://www.w3schools.com/html/html5_webstorage.asp">HTML 5
	 * Webstorage technology</a>.
	 * 
	 * @param driver
	 *            The driver to communicate over
	 * @throws DriverStorageUnsupportedException
	 *             If the given driver does not support <a href=
	 *             "https://www.w3schools.com/html/html5_webstorage.asp">HTML 5
	 *             Webstorage technology</a>. That is the case if the driver
	 *             does not implement {@link WebStorage} nor
	 *             {@link JavascriptExecutor}.
	 */
	public DataBridge(final WebDriver driver) throws DriverStorageUnsupportedException {
		// Get storage from driver
		SessionStorage sessionStorage = null;

		// Get the underlying raw driver
		WebDriver rawDriver = driver;
		while (rawDriver instanceof IWrapsWebDriver) {
			rawDriver = ((IWrapsWebDriver) rawDriver).getRawDriver();
		}

		if (rawDriver instanceof WebStorage) {
			sessionStorage = ((WebStorage) rawDriver).getSessionStorage();
		} else if (rawDriver instanceof JavascriptExecutor) {
			sessionStorage = new JavaScriptSessionStorage((JavascriptExecutor) rawDriver);
		}

		if (sessionStorage == null) {
			throw new DriverStorageUnsupportedException(rawDriver);
		}

		this.mStorage = sessionStorage;
	}

	/**
	 * Clears the problem flag which can be set by
	 * {@link #setProblem(Exception, long)}.
	 */
	public void clearProblem() {
		this.mStorage.setItem(buildKey(StorageKeys.PROBLEM), "");
	}

	/**
	 * Clears the start signal flag.
	 */
	public void clearStartSignal() {
		this.mStorage.removeItem(buildKey(StorageKeys.START_SIGNAL));
	}

	/**
	 * Clears the stop signal flag.
	 */
	public void clearStopSignal() {
		this.mStorage.removeItem(buildKey(StorageKeys.STOP_SIGNAL));
	}

	/**
	 * Whether the start signal flag is set or not.
	 * 
	 * @return <tt>True</tt> if the start signal flag is set, <tt>false</tt> if
	 *         not
	 */
	public boolean isStartSignalSet() {
		final String value = this.mStorage.getItem(buildKey(StorageKeys.START_SIGNAL));
		return Boolean.valueOf(value).booleanValue();
	}

	/**
	 * Whether the stop signal flag is set or not.
	 * 
	 * @return <tt>True</tt> if the stop signal flag is set, <tt>false</tt> if
	 *         not
	 */
	public boolean isStopSignalSet() {
		final String value = this.mStorage.getItem(buildKey(StorageKeys.STOP_SIGNAL));
		return Boolean.valueOf(value).booleanValue();
	}

	/**
	 * Pushes the given item entry into the communication channel.
	 * 
	 * @param entry
	 *            The item entry to push
	 */
	public void pushItemEntry(final ItemEntry entry) {
		// Build entry text
		final StringBuilder entryValue = new StringBuilder();
		entryValue.append(entry.getTimestamp()).append(ITEM_VALUE_SEPARATOR);
		entryValue.append(entry.getItem()).append(ITEM_VALUE_SEPARATOR);
		entryValue.append(entry.getCost()).append(ITEM_VALUE_SEPARATOR);
		entryValue.append(entry.getProfit()).append(ITEM_VALUE_SEPARATOR);
		entryValue.append(entry.wasCached()).append(ITEM_VALUE_SEPARATOR);
		entryValue.append(entry.isConsideredForShop());

		// Get current stored text
		String value = this.mStorage.getItem(buildKey(StorageKeys.ITEM_ENTRIES));

		// Push entry
		if (value == null) {
			value = "";
		}
		if (!value.isEmpty()) {
			value += ITEM_ENTRY_SEPARATOR;
		}
		value += entryValue.toString();

		this.mStorage.setItem(buildKey(StorageKeys.ITEM_ENTRIES), value);
	}

	/**
	 * Sets the active flag. Indicates whether the service is running. Should be
	 * triggered by the start signal flag, see also {@link #isStartSignalSet()}.
	 * 
	 * @param isActive
	 *            <tt>True</tt> if the service is running, <tt>false</tt>
	 *            otherwise
	 */
	public void setActive(final boolean isActive) {
		this.mStorage.setItem(buildKey(StorageKeys.IS_ACTIVE), isActive + "");
	}

	/**
	 * Sets the tool serving flag. Indicates whether the tool is alive and
	 * running. Used for the web interface to know when to start and stop its
	 * own service.
	 * 
	 * @param isBeedleBotServing
	 *            <tt>True</tt> if the tool is alive and running, <tt>false</tt>
	 *            otherwise
	 */
	public void setBeedleBotServing(final boolean isBeedleBotServing) {
		this.mStorage.setItem(buildKey(StorageKeys.IS_BEEDLE_BOT_SERVING), isBeedleBotServing + "");
	}

	/**
	 * Sets the current lifepoints to the communication channel.
	 * 
	 * @param currentLifepoints
	 *            The current lifepoints to set
	 */
	public void setCurrentLifepoints(final int currentLifepoints) {
		this.mStorage.setItem(buildKey(StorageKeys.CUR_LIFEPOINTS), currentLifepoints + "");
	}

	/**
	 * Sets the gold to the communication channel.
	 * 
	 * @param gold
	 *            The gold to set
	 */
	public void setGold(final int gold) {
		this.mStorage.setItem(buildKey(StorageKeys.GOLD), gold + "");
	}

	/**
	 * Sets the inventory size to the communication channel.
	 * 
	 * @param size
	 *            The size to set
	 */
	public void setInventorySize(final int size) {
		this.mStorage.setItem(buildKey(StorageKeys.INVENTORY_SIZE), size + "");
	}

	/**
	 * Sets the maximal inventory size to the communication channel.
	 * 
	 * @param maxInventorySize
	 *            The maximal inventory size to set
	 */
	public void setMaxInventorySize(final int maxInventorySize) {
		this.mStorage.setItem(buildKey(StorageKeys.MAX_INVENTORY_SIZE), maxInventorySize + "");
	}

	/**
	 * Sets the maximal lifepoints to the communication channel.
	 * 
	 * @param maxLifepoints
	 *            The maximal lifepoints to set
	 */
	public void setMaxLifepoints(final int maxLifepoints) {
		this.mStorage.setItem(buildKey(StorageKeys.MAX_LIFEPOINTS), maxLifepoints + "");
	}

	/**
	 * Sets the given phase to the communication channel.
	 * 
	 * @param phase
	 *            The phase to set
	 */
	public void setPhase(final EPhase phase) {
		this.mStorage.setItem(buildKey(StorageKeys.PHASE), phase.toString());
	}

	/**
	 * Sets the given problem to the communication channel. If the problem was
	 * resolved call {@link #clearProblem()}.
	 * 
	 * @param problem
	 *            The problem to set
	 * @param timestamp
	 *            The timestamp of the problem
	 */
	public void setProblem(final Exception problem, final long timestamp) {
		final StringBuilder problemText = new StringBuilder();
		final Date date = new Date(timestamp);
		final String timestampFormat = DateFormat.getDateTimeInstance().format(date);

		problemText.append(timestampFormat);
		problemText.append(": ");
		problemText.append(LoggerUtil.getStackTrace(problem));

		this.mStorage.setItem(buildKey(StorageKeys.PROBLEM), problemText.toString());
	}

	/**
	 * Sets the given state to the communication channel.
	 * 
	 * @param state
	 *            The state to set
	 */
	public void setState(final EState state) {
		this.mStorage.setItem(buildKey(StorageKeys.STATE), state.toString());
	}

	/**
	 * Sets the total cost value to the communication channel.
	 * 
	 * @param totalCost
	 *            The total cost value to set
	 */
	public void setTotalCost(final int totalCost) {
		this.mStorage.setItem(buildKey(StorageKeys.TOTAL_COST), totalCost + "");
	}

	/**
	 * Sets the total profit value to the communication channel.
	 * 
	 * @param totalProfit
	 *            The total profit value to set
	 */
	public void setTotalProfit(final int totalProfit) {
		this.mStorage.setItem(buildKey(StorageKeys.TOTAL_PROFIT), totalProfit + "");
	}

	/**
	 * Sets the waiting time value to the communication channel.
	 * 
	 * @param waitingTime
	 *            The waiting time value to set
	 */
	public void setWaitingTime(final int waitingTime) {
		this.mStorage.setItem(buildKey(StorageKeys.WAITING_TIME), waitingTime + "");
	}

	/**
	 * Sends a heart-beat signal to the communication channel. This should be
	 * done frequently else the web interface will assume that the tool has
	 * crashed.
	 */
	public void updateHeartBeat() {
		this.mStorage.setItem(buildKey(StorageKeys.HEART_BEAT), System.currentTimeMillis() + "");
	}

}
