package de.zabuza.beedlebot.service.routine.tasks;

import de.zabuza.beedlebot.service.routine.CentralTradersDepotNavigator;
import de.zabuza.beedlebot.store.Item;

public final class PurchaseTask implements ITask {
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	private final Item mItem;
	private final CentralTradersDepotNavigator mNavigator;
	private boolean mWasBought;

	public PurchaseTask(final CentralTradersDepotNavigator nagivator, final Item item) {
		mNavigator = nagivator;
		mItem = item;
		mInterrupted = false;
		mWasBought = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#interrupt()
	 */
	@Override
	public void interrupt() {
		mInterrupted = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#isInterrupted()
	 */
	@Override
	public boolean isInterrupted() {
		return mInterrupted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#start()
	 */
	@Override
	public void start() {
		final boolean wasCategoryClicked = mNavigator.openItemCategory(mItem.getItemCategory());
		// TODO Correct error handling and logging
		if (!wasCategoryClicked) {
			mNavigator.exitMenu();
			// TODO Exchange with a more specific exception
			throw new IllegalStateException();
		}

		// Click the purchase anchor
		final boolean wasBought = mNavigator.purchaseItem(mItem.getPurchaseAnchor());
		if (!wasBought) {
			// Item is not present anymore, another player may already have
			// bought it
			mNavigator.exitMenu();
			return;
		}

		// Click the continue anchor
		final boolean wasContinueClicked = mNavigator.exitPurchasedDialog();

		// TODO Correct error handling and logging
		if (!wasContinueClicked) {
			mNavigator.exitMenu();
			// TODO Exchange with a more specific exception
			throw new IllegalStateException();
		}

		mWasBought = true;
	}

	public boolean wasBought() {
		return mWasBought;
	}
}
