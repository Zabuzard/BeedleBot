package de.zabuza.beedlebot.databridge.io;

import java.util.Queue;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

import de.zabuza.beedlebot.databridge.DataBridge;
import de.zabuza.beedlebot.databridge.EPhase;
import de.zabuza.beedlebot.databridge.EState;
import de.zabuza.beedlebot.databridge.ItemEntry;
import de.zabuza.beedlebot.service.Service;
import de.zabuza.beedlebot.service.routine.Routine;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.sparkle.freewar.IFreewarInstance;
import de.zabuza.sparkle.freewar.inventory.IInventory;
import de.zabuza.sparkle.freewar.player.IPlayer;

/**
 * Service which pushes data from the tool to the tools web interface via a
 * {@link DataBridge}. After creating call {@link #linkRoutine(Routine)} to link
 * a routine to this service. Call {@link #update()} to push data from tool to
 * the bridge. The service has two update cycles, they can be accessed with
 * {@link #updateActiveData()} and {@link #updatePassiveData()}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class PushDataService {
	/**
	 * The standard waiting time after purchasing an item at the central traders
	 * depot.
	 */
	private static final int WAITING_TIME_MIN = 10;
	/**
	 * The data bridge to use for accessing and setting data.
	 */
	private final DataBridge mDataBridge;
	/**
	 * The current Freewar instance of the tool.
	 */
	private final IFreewarInstance mInstance;
	/**
	 * The routine object to use for accessing the state of the tool.
	 */
	private Routine mRoutine;
	/**
	 * The service object to use for accessing the state of the tool.
	 */
	private final Service mService;

	/**
	 * Creates a new push data service which uses the given data bridge to
	 * access data. After creating call {@link #linkRoutine(Routine)} to link a
	 * routine to this service. Call {@link #update()} to push the data of the
	 * tool to the web interface. Use {@link #updateActiveData()} to only push
	 * important data and {@link #updatePassiveData()} to also push the rest.
	 * 
	 * @param service
	 *            The service object to use for accessing the state of the tool
	 * @param instance
	 *            The current Freewar instance of the tool
	 * @param dataBridge
	 *            The data bridge to use for accessing and setting data
	 */
	public PushDataService(final Service service, final IFreewarInstance instance, final DataBridge dataBridge) {
		this.mService = service;
		this.mInstance = instance;
		this.mDataBridge = dataBridge;

		this.mRoutine = null;
	}

	/**
	 * Links the given routine to this service. It is used to access the state
	 * of the tool.
	 * 
	 * @param routine
	 *            The routine to link
	 */
	public void linkRoutine(final Routine routine) {
		this.mRoutine = routine;
	}

	/**
	 * Sets whether the tool is alive and running.
	 * 
	 * @param isBeedleBotServing
	 *            <tt>True</tt> if the tool is alive and running, <tt>false</tt>
	 *            if not
	 */
	public void setBeedleBotServing(final boolean isBeedleBotServing) {
		this.mDataBridge.setBeedleBotServing(isBeedleBotServing);
	}

	/**
	 * Updates the data by pushing the it to the data bridge. This method calls
	 * both update cycles, {@link #updateActiveData()} and
	 * {@link #updatePassiveData()}.
	 */
	public void update() {
		updateActiveData();
		updatePassiveData();
	}

	/**
	 * Updates only the important data like state information by pushing it to
	 * the data bridge.
	 */
	public void updateActiveData() {
		if (this.mRoutine == null) {
			return;
		}

		final boolean isActive = !this.mService.isPaused() && this.mService.isActive();

		// Push active flag
		this.mDataBridge.setActive(isActive);

		// Determine phase
		EPhase phase = this.mRoutine.getPhase();

		// Determine state
		final EState state;
		if (this.mService.hasProblem()) {
			state = EState.PROBLEM;
			phase = EPhase.AWAITING_DELIVERY;
			this.mDataBridge.setProblem(this.mService.getProblem(), this.mService.getProblemTimestamp());
		} else {
			this.mDataBridge.clearProblem();
			if (!isActive) {
				state = EState.INACTIVE;
				phase = EPhase.AWAITING_DELIVERY;
			} else if (phase == EPhase.AWAITING_DELIVERY) {
				state = EState.STANDBY;
			} else {
				state = EState.ACTIVE;
			}
		}

		this.mDataBridge.setState(state);
		this.mDataBridge.setPhase(phase);
	}

	/**
	 * Updates passive data like player statistics and values by pushing it to
	 * the data bridge.
	 */
	public void updatePassiveData() {
		if (this.mRoutine == null) {
			return;
		}

		try {
			final IPlayer player = this.mInstance.getPlayer();
			final IInventory inventory = this.mInstance.getInventory();

			// Update heartbeat
			this.mDataBridge.updateHeartBeat();

			// Get lifepoints
			this.mDataBridge.setCurrentLifepoints(player.getLifePoints());
			this.mDataBridge.setMaxLifepoints(player.getMaxLifePoints());

			// Get gold
			this.mDataBridge.setGold(player.getGold());

			// Get inventory size
			final int speed = player.getSpeed();
			final int inventorySize = inventory.getInventorySize();
			this.mDataBridge.setMaxInventorySize(speed);
			this.mDataBridge.setInventorySize(inventorySize);

			// Compute waiting time
			final int itemsMoreThanSpeed = inventorySize - speed;
			final int waitingTime;
			if (itemsMoreThanSpeed > 0) {
				waitingTime = WAITING_TIME_MIN + itemsMoreThanSpeed;
			} else {
				waitingTime = WAITING_TIME_MIN;
			}
			this.mDataBridge.setWaitingTime(waitingTime);

			// Push item-entries
			final Queue<Item> boughtItems = this.mRoutine.fetchBoughtItems();
			for (final Item item : boughtItems) {
				this.mDataBridge.pushItemEntry(new ItemEntry(item.getName(), item.getCost(), item.getProfit(),
						item.getStorePriceData().isCached(), item.isConsideredForShop()));
			}

			// Get total cost
			this.mDataBridge.setTotalCost(this.mRoutine.getTotalCost());

			// Get total profit
			this.mDataBridge.setTotalProfit(this.mRoutine.getTotalProfit());
		} catch (final NoSuchElementException | StaleElementReferenceException | TimeoutException e) {
			// Frame seems to have changed its content. Maybe the player
			// interacted with it. Simply ignore the problem and yield this
			// iteration, hoping it will resolve automatically.
		}
	}
}
