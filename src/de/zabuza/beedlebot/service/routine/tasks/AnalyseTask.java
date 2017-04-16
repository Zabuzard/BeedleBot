package de.zabuza.beedlebot.service.routine.tasks;

import java.util.Optional;

import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.service.routine.AnalyseResult;
import de.zabuza.beedlebot.service.routine.CentralTradersDepotNavigator;
import de.zabuza.beedlebot.store.EItemCategory;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.beedlebot.store.ItemPrice;
import de.zabuza.beedlebot.store.PlayerPrice;
import de.zabuza.beedlebot.store.Store;

public final class AnalyseTask implements ITask {
	private static final String CONTENT_BUY_ANCHOR_END = "\"> kaufen";
	private static final String CONTENT_BUY_ANCHOR_START = "Gold <a href=\"";
	private static final String CONTENT_ID_START = "mit_item=";
	private static final String CONTENT_IS_MAGICAL_PRESENCE = "class=\"itemmagic\"";
	private static final String CONTENT_ITEM_COST_END = " Gold";
	private static final String CONTENT_ITEM_COST_START = "für ";
	private static final String CONTENT_ITEM_NAME_END = "</b>";
	private static final String CONTENT_ITEM_NAME_START = "<b>";
	private static final String CONTENT_LINE_SPLIT_PATTERN = "(<br\\s*/>|<br>)";
	private static final String CONTENT_LINE_VALIDATOR = "<b>";
	private static final String CONTENT_NEEDLE_END = "Zurück";
	private static final String CONTENT_NEEDLE_START = "Zurück";
	private final WebDriver mDriver;
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	private final EItemCategory mItemCategory;
	private final CentralTradersDepotNavigator mNavigator;
	private final AnalyseResult mResult;
	private final Store mStore;

	public AnalyseTask(final WebDriver driver, final AnalyseResult result, final EItemCategory itemCategory,
			final Store store, final CentralTradersDepotNavigator navigator) {
		mInterrupted = false;
		mDriver = driver;
		mResult = result;
		mItemCategory = itemCategory;
		mStore = store;
		mNavigator = navigator;
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
		// Open category
		final boolean wasClicked = mNavigator.openItemCategory(mItemCategory);

		if (!wasClicked) {
			mNavigator.exitMenu();
			// TODO Exchange with a more specific exception
			throw new IllegalStateException();
		}

		// Retrieve content
		final String content = mDriver.getPageSource();

		// Process content
		processContent(content);

		// Finish the task
		mNavigator.exitMenu();
	}

	private void processContent(final String content) {
		// Strip everything between start and end needle
		final int startIndexRaw = content.indexOf(CONTENT_NEEDLE_START);
		final int startIndex = startIndexRaw + CONTENT_NEEDLE_START.length();
		final int endIndex = content.indexOf(CONTENT_NEEDLE_END, startIndex);

		if (startIndexRaw == -1 || endIndex == -1) {
			// TODO Exchange with a more specific exception
			throw new IllegalStateException();
		}

		final String itemContent = content.substring(startIndex, endIndex);
		final String[] itemContentLines = itemContent.split(CONTENT_LINE_SPLIT_PATTERN);
		for (final String itemContentLine : itemContentLines) {
			// Reject if line does not begin with validator
			if (!itemContentLine.startsWith(CONTENT_LINE_VALIDATOR)) {
				continue;
			}

			// Extract item name
			final int itemNameStart = itemContentLine.indexOf(CONTENT_ITEM_NAME_START);
			final int itemNameEnd = itemContentLine.indexOf(CONTENT_ITEM_NAME_END);

			if (itemNameStart == -1 || itemNameEnd == -1) {
				// TODO Exchange with a more specific exception
				throw new IllegalStateException();
			}

			final String itemName = itemContentLine.substring(itemNameStart + CONTENT_ITEM_NAME_START.length(),
					itemNameEnd);

			// Extract cost
			final int itemCostStart = itemContentLine.indexOf(CONTENT_ITEM_COST_START);
			final int itemCostEnd = itemContentLine.indexOf(CONTENT_ITEM_COST_END);

			if (itemCostStart == -1 || itemCostEnd == -1) {
				// TODO Exchange with a more specific exception
				throw new IllegalStateException();
			}

			String itemCostText = itemContentLine.substring(itemCostStart + CONTENT_ITEM_COST_START.length(),
					itemCostEnd);
			// Remove thousand separator
			itemCostText = itemCostText.replaceAll("\\.", "");
			final Integer itemCost = Integer.parseInt(itemCostText);

			// Extract purchase anchor
			final int purchaseAnchorStart = itemContentLine.indexOf(CONTENT_BUY_ANCHOR_START);
			final int purchaseAnchorEnd = itemContentLine.indexOf(CONTENT_BUY_ANCHOR_END);

			if (purchaseAnchorStart == -1 || purchaseAnchorEnd == -1) {
				// TODO Exchange with a more specific exception
				throw new IllegalStateException();
			}

			final String purchaseAnchor = itemContentLine
					.substring(purchaseAnchorStart + CONTENT_BUY_ANCHOR_START.length(), purchaseAnchorEnd);
			final String purchaseAnchorDecoded = purchaseAnchor.replaceAll("&amp;", "&");

			// Extract id from purchase anchor
			final int idStart = purchaseAnchor.indexOf(CONTENT_ID_START);

			if (idStart == -1) {
				// TODO Exchange with a more specific exception
				throw new IllegalStateException();
			}

			final int id = Integer.parseInt(purchaseAnchor.substring(idStart + CONTENT_ID_START.length()));

			// Extract is magical state
			final boolean isMagical = itemContentLine.contains(CONTENT_IS_MAGICAL_PRESENCE);

			// Determine profit
			final ItemPrice itemPriceData = mStore.getItemPrice(itemName);
			final boolean isConsideredForShop = mStore.isItemConsideredForShop(itemName, itemCost, itemPriceData);
			final int itemProfit;
			if (isConsideredForShop) {
				final int standardShopPrice = itemPriceData.getStandardShopPrice();
				itemProfit = Store.computeFullShopPrice(standardShopPrice) - itemCost;
			} else {
				final Optional<PlayerPrice> playerPrice = itemPriceData.getPlayerPrice();
				if (playerPrice.isPresent()) {
					itemProfit = playerPrice.get().getPrice() - itemCost;
				} else {
					// TODO Exchange with a more specific exception
					throw new IllegalStateException();
				}
			}

			final Item item = new Item(itemName, itemCost, itemProfit, id, purchaseAnchorDecoded, isMagical,
					isConsideredForShop, itemPriceData, mItemCategory);

			// Add the item to the analyse result if accepted
			if (mStore.isItemAccepted(item)) {
				mResult.add(item);
			}
		}
	}
}
