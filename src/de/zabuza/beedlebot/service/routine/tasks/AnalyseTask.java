package de.zabuza.beedlebot.service.routine.tasks;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.service.routine.AnalyseResult;
import de.zabuza.beedlebot.service.routine.EItemCategory;
import de.zabuza.beedlebot.service.routine.Item;
import de.zabuza.beedlebot.store.ItemPrice;
import de.zabuza.beedlebot.store.Store;
import de.zabuza.sparkle.freewar.IFreewarInstance;
import de.zabuza.sparkle.freewar.frames.EFrame;
import de.zabuza.sparkle.wait.EventQueueEmptyWait;

public final class AnalyseTask implements ITask {
	private static final String ABORT_ANCHOR = "Zur";
	private static final String AMULET_CATEGORY_ANCHOR = "Kategorie: Halsschmuck";
	private static final String ATTACK_WEAPON_CATEGORY_ANCHOR = "Kategorie: Angriffswaffen";
	private static final int CONSIDER_PLAYER_PRICE_COST_ABS = 200;
	private static final String CONTENT_BUY_ANCHOR_END = "\"> kaufen";
	private static final String CONTENT_BUY_ANCHOR_START = "Gold <a href=\"";
	private static final String CONTENT_IS_MAGICAL_PRESENCE = "class=\"itemmagic\"";
	private static final String CONTENT_ITEM_COST_END = " Gold";
	private static final String CONTENT_ITEM_COST_START = "für ";
	private static final String CONTENT_ITEM_NAME_END = "</b>";
	private static final String CONTENT_ITEM_NAME_START = "<b>";
	private static final String CONTENT_LINE_SPLIT = "<br />";
	private static final String CONTENT_LINE_VALIDATOR = "<b>";
	private static final String CONTENT_NEEDLE_END = "Zurück";
	private static final String CONTENT_NEEDLE_START = "Zurück";
	private static final String DEFENSE_WEAPON_CATEGORY_ANCHOR = "Kategorie: Verteidigungswaffen";
	private static final String MISCELLANEOUS_CATEGORY_ANCHOR = "Kategorie: Sonstiges";
	private static final String SPELL_CATEGORY_ANCHOR = "Kategorie: Anwendbare Items und Zauber";
	private final WebDriver mDriver;
	private final IFreewarInstance mInstance;
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	private final EItemCategory mItemCategory;
	private final Map<EItemCategory, String> mItemCategoryToAnchorNeedle;
	private final AnalyseResult mResult;
	private final Store mStore;

	public AnalyseTask(final IFreewarInstance instance, final WebDriver driver, final AnalyseResult result,
			final EItemCategory itemCategory, final Store store) {
		mInterrupted = false;
		mInstance = instance;
		mDriver = driver;
		mResult = result;
		mItemCategory = itemCategory;
		mStore = store;

		// TODO Remove code duplication with purchase task
		mItemCategoryToAnchorNeedle = new HashMap<>();
		mItemCategoryToAnchorNeedle.put(EItemCategory.ATTACK_WEAPON, ATTACK_WEAPON_CATEGORY_ANCHOR);
		mItemCategoryToAnchorNeedle.put(EItemCategory.DEFENSE_WEAPON, DEFENSE_WEAPON_CATEGORY_ANCHOR);
		mItemCategoryToAnchorNeedle.put(EItemCategory.AMULET, AMULET_CATEGORY_ANCHOR);
		mItemCategoryToAnchorNeedle.put(EItemCategory.SPELL, SPELL_CATEGORY_ANCHOR);
		mItemCategoryToAnchorNeedle.put(EItemCategory.MISCELLANEOUS, MISCELLANEOUS_CATEGORY_ANCHOR);
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
		final String needle = mItemCategoryToAnchorNeedle.get(mItemCategory);
		final boolean wasClicked = mInstance.clickAnchorByContent(EFrame.MAIN, needle);

		// TODO Correct error handling and logging
		if (!wasClicked) {
			return;
		}

		// Wait for click to get executed
		new EventQueueEmptyWait(mDriver).waitUntilCondition();

		// Retrieve content
		final String content = mDriver.getPageSource();

		// Process content
		processContent(content);

		// Finish the task
		if (!mInstance.clickAnchorByContent(EFrame.MAIN, ABORT_ANCHOR)) {
			// TODO Correct error handling and logging
			return;
		}

		// Wait for click to get executed
		new EventQueueEmptyWait(mDriver).waitUntilCondition();
	}

	private void processContent(final String content) {
		// Strip everything between start and end needle
		final int startIndexRaw = content.indexOf(CONTENT_NEEDLE_START);
		final int startIndex = startIndexRaw + CONTENT_NEEDLE_START.length();
		final int endIndex = content.indexOf(CONTENT_NEEDLE_END, startIndex);

		if (startIndexRaw == -1 || endIndex == -1) {
			// TODO Correct error handling and logging
			return;
		}

		final String itemContent = content.substring(startIndex, endIndex);
		final String[] itemContentLines = itemContent.split(CONTENT_LINE_SPLIT);
		for (final String itemContentLine : itemContentLines) {
			// Reject if line does not begin with validator
			if (!itemContentLine.startsWith(CONTENT_LINE_VALIDATOR)) {
				continue;
			}

			// Extract item name
			final int itemNameStart = itemContentLine.indexOf(CONTENT_ITEM_NAME_START);
			final int itemNameEnd = itemContentLine.indexOf(CONTENT_ITEM_NAME_END);

			if (itemNameStart == -1 || itemNameEnd == -1) {
				// TODO Correct error handling and logging
				return;
			}

			final String itemName = itemContentLine.substring(itemNameStart + CONTENT_ITEM_NAME_START.length(),
					itemNameEnd);

			// Extract cost
			final int itemCostStart = itemContentLine.indexOf(CONTENT_ITEM_COST_START);
			final int itemCostEnd = itemContentLine.indexOf(CONTENT_ITEM_COST_END);

			if (itemCostStart == -1 || itemCostEnd == -1) {
				// TODO Correct error handling and logging
				return;
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
				// TODO Correct error handling and logging
				return;
			}

			final String purchaseAnchor = itemContentLine
					.substring(purchaseAnchorStart + CONTENT_BUY_ANCHOR_START.length(), purchaseAnchorEnd);

			// Extract is magical state
			final boolean isMagical = itemContentLine.contains(CONTENT_IS_MAGICAL_PRESENCE);
			// Reject item if magical
			if (isMagical) {
				continue;
			}

			// Determine profit
			final ItemPrice itemPriceData = mStore.getItemPrice(itemName);
			// TODO Filtering and logic when to use playerPrice
			// TODO Out-source logic to better location
			final int standardShopPrice = itemPriceData.getShopPrice();
			final int shopPrice = Store.computeFullShopPrice(standardShopPrice);
			final int itemPrice;
			if (itemPriceData.hasPlayerPrice()) {
				final int playerPrice = itemPriceData.getPlayerPrice().get().getPrice();
				if (playerPrice - itemCost >= CONSIDER_PLAYER_PRICE_COST_ABS) {
					itemPrice = Math.max(shopPrice, playerPrice);
				} else {
					itemPrice = shopPrice;
				}
			} else {
				itemPrice = shopPrice;
			}
			final int itemProfit = itemPrice - itemCost;

			// Reject item if price is below cost
			if (itemProfit <= 0) {
				continue;
			}

			// Add the item to the analyse result
			mResult.add(
					new Item(itemName, itemCost, itemProfit, purchaseAnchor, isMagical, itemPriceData, mItemCategory));
		}
	}
}
