package de.zabuza.beedlebot.service.routine.tasks;

import de.zabuza.beedlebot.exceptions.ItemCategoryNotOpenedException;
import de.zabuza.beedlebot.exceptions.PurchaseDialogNotClosedException;
import de.zabuza.beedlebot.exceptions.PurchaseNoGoldException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.service.routine.CentralTradersDepotNavigator;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.sparkle.freewar.player.IPlayer;

public final class PurchaseTask implements ITask {
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	private final Item mItem;
	private final ILogger mLogger;
	private final CentralTradersDepotNavigator mNavigator;
	private final IPlayer mPlayer;
	private boolean mWasBought;

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

	public boolean wasBought() {
		return this.mWasBought;
	}
}
