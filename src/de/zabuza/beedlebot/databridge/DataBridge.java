package de.zabuza.beedlebot.databridge;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.html5.SessionStorage;

import de.zabuza.beedlebot.exceptions.DriverStorageUnsupportedException;
import de.zabuza.sparkle.webdriver.IWrapsWebDriver;

/**
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class DataBridge {

	private static final String ITEM_ENTRY_SEPARATOR = ";";
	private static final String ITEM_VALUE_SEPARATOR = "?";
	private final SessionStorage mStorage;

	public DataBridge(final WebDriver driver) throws DriverStorageUnsupportedException {
		// Get storage from chrome driver
		ChromeDriver chromeDriver = null;

		WebDriver rawDriver = driver;
		while (rawDriver instanceof IWrapsWebDriver) {
			rawDriver = ((IWrapsWebDriver) rawDriver).getRawDriver();
		}
		if (rawDriver instanceof ChromeDriver) {
			chromeDriver = (ChromeDriver) rawDriver;
		}

		if (chromeDriver == null) {
			throw new DriverStorageUnsupportedException(driver);
		}

		mStorage = chromeDriver.getSessionStorage();
	}

	public void clearStartSignal() {
		mStorage.removeItem(buildKey(StorageKeys.START_SIGNAL));
	}

	public void clearStopSignal() {
		mStorage.removeItem(buildKey(StorageKeys.STOP_SIGNAL));
	}

	public boolean isStartSignalSet() {
		final String value = mStorage.getItem(buildKey(StorageKeys.START_SIGNAL));
		return Boolean.valueOf(value).booleanValue();
	}

	public boolean isStopSignalSet() {
		final String value = mStorage.getItem(buildKey(StorageKeys.STOP_SIGNAL));
		return Boolean.valueOf(value).booleanValue();
	}

	public void pushItemEntry(final ItemEntry entry) {
		// Build entry text
		StringBuilder entryValue = new StringBuilder();
		entryValue.append(entry.getTimestamp()).append(ITEM_VALUE_SEPARATOR);
		entryValue.append(entry.getItem()).append(ITEM_VALUE_SEPARATOR);
		entryValue.append(entry.getCost()).append(ITEM_VALUE_SEPARATOR);
		entryValue.append(entry.getProfit()).append(ITEM_VALUE_SEPARATOR);
		entryValue.append(entry.wasCached()).append(ITEM_VALUE_SEPARATOR);
		entryValue.append(entry.isConsideredForShop());

		// Get current stored text
		String value = mStorage.getItem(buildKey(StorageKeys.ITEM_ENTRIES));

		// Push entry
		if (value == null) {
			value = "";
		}
		if (!value.isEmpty()) {
			value += ITEM_ENTRY_SEPARATOR;
		}
		value += entryValue.toString();

		mStorage.setItem(buildKey(StorageKeys.ITEM_ENTRIES), value);
	}

	public void setActive(final boolean isActive) {
		mStorage.setItem(buildKey(StorageKeys.IS_ACTIVE), isActive + "");
	}

	public void setBeedleBotServing(final boolean isBeedleBotServing) {
		mStorage.setItem(buildKey(StorageKeys.IS_BEEDLE_BOT_SERVING), isBeedleBotServing + "");
	}

	public void setCurrentLifepoints(final int currentLifepoints) {
		mStorage.setItem(buildKey(StorageKeys.CUR_LIFEPOINTS), currentLifepoints + "");
	}

	public void setGold(final int gold) {
		mStorage.setItem(buildKey(StorageKeys.GOLD), gold + "");
	}

	public void setInventorySize(final int size) {
		mStorage.setItem(buildKey(StorageKeys.INVENTORY_SIZE), size + "");
	}

	public void setMaxInventorySize(final int maxSize) {
		mStorage.setItem(buildKey(StorageKeys.MAX_INVENTORY_SIZE), maxSize + "");
	}

	public void setMaxLifepoints(final int maxLifepoints) {
		mStorage.setItem(buildKey(StorageKeys.MAX_LIFEPOINTS), maxLifepoints + "");
	}

	public void setPhase(final EPhase phase) {
		mStorage.setItem(buildKey(StorageKeys.PHASE), phase.toString());
	}

	public void setState(final EState state) {
		mStorage.setItem(buildKey(StorageKeys.STATE), state.toString());
	}

	public void setTotalCost(final int totalCost) {
		mStorage.setItem(buildKey(StorageKeys.TOTAL_COST), totalCost + "");
	}

	public void setTotalProfit(final int totalProfit) {
		mStorage.setItem(buildKey(StorageKeys.TOTAL_PROFIT), totalProfit + "");
	}

	public void setWaitingTime(final int waitingTime) {
		mStorage.setItem(buildKey(StorageKeys.WAITING_TIME), waitingTime + "");
	}

	public void updateHeartBeat() {
		mStorage.setItem(buildKey(StorageKeys.HEART_BEAT), System.currentTimeMillis() + "");
	}

	private String buildKey(final String key) {
		return StorageKeys.KEY_INDEX + key;
	}

}
