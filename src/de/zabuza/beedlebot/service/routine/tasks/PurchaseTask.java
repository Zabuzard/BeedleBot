package de.zabuza.beedlebot.service.routine.tasks;

import de.zabuza.beedlebot.exceptions.ItemCategoryNotOpenedException;
import de.zabuza.beedlebot.exceptions.PurchaseDialogNotClosedException;
import de.zabuza.beedlebot.exceptions.PurchaseNoGoldException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.service.routine.CentralTradersDepotNavigator;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.sparkle.freewar.player.IPlayer;

/**
 * Task that purchases a given item from the central traders depot. After
 * creation use {@link #start()} to start the task and {@link #wasBought()} to
 * fetch the resulting state. After this task has ended it should not be used
 * anymore, instead create a new instance.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class PurchaseTask implements ITask {
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	/**
	 * The item to purchase from the central traders depot.
	 */
	private final Item mItem;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;
	/**
	 * The navigator to use for navigating menus at the central traders depot.
	 */
	private final CentralTradersDepotNavigator mNavigator;
	/**
	 * The player to use for accessing the current amount of gold.
	 */
	private final IPlayer mPlayer;
	/**
	 * Whether the item was successfully purchased or not.
	 */
	private boolean mWasBought;

	/**
	 * Creates a new purchase task that purchases the given item from the
	 * central traders depot. After creation use {@link #start()} to start the
	 * task and {@link #wasBought()} to fetch the resulting state.
	 * 
	 * @param nagivator
	 *            The navigator to use for navigating menus at the central
	 *            traders depot
	 * @param player
	 *            The player to use for accessing the current amount of gold
	 * @param item
	 *            The item to purchase from the central traders depot
	 */
	public PurchaseTask(final CentralTradersDepotNavigator nagivator, final IPlayer player, final Item item) {
		this.mNavigator = nagivator;
		this.mPlayer = player;
		this.mItem = item;
		this.mInterrupted = false;
		this.mWasBought = false;
		this.mLogger = LoggerFactory.getLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#interrupt()
	 */
	@Override
	public void interrupt() {
		this.mInterrupted = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#isInterrupted()
	 */
	@Override
	public boolean isInterrupted() {
		return this.mInterrupted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#start()
	 */
	@Override
	public void start() throws ItemCategoryNotOpenedException, PurchaseDialogNotClosedException {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Starting PurchaseTask");
		}

		// Check if player has enough gold to buy the item
		final int currentGold = this.mPlayer.getGold();
		if (currentGold < this.mItem.getCost()) {
			throw new PurchaseNoGoldException();
		}

		final boolean wasCategoryClicked = this.mNavigator.openItemCategory(this.mItem.getItemCategory());
		if (!wasCategoryClicked) {
			this.mNavigator.exitMenu();
			throw new ItemCategoryNotOpenedException();
		}

		// Click the purchase anchor
		final boolean wasBought = this.mNavigator.purchaseItem(this.mItem.getPurchaseAnchor());
		if (!wasBought) {
			// Item is not present anymore, another player may already have
			// bought it
			this.mNavigator.exitMenu();
			return;
		}

		// Click the continue anchor
		final boolean wasContinueClicked = this.mNavigator.exitPurchasedDialog();

		if (!wasContinueClicked) {
			this.mNavigator.exitMenu();
			throw new PurchaseDialogNotClosedException();
		}

		this.mWasBought = true;
	}

	/**
	 * Whether the item was successfully bought or not.
	 * 
	 * @return <tt>True</tt> if the item was successfully bought, <tt>false</tt>
	 *         if not
	 */
	public boolean wasBought() {
		return this.mWasBought;
	}
}
