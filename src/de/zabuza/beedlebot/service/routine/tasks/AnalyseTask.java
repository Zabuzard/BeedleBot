package de.zabuza.beedlebot.service.routine.tasks;

import java.util.Optional;

import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.exceptions.ItemCategoryNotOpenedException;
import de.zabuza.beedlebot.exceptions.ItemLineWrongFormatException;
import de.zabuza.beedlebot.exceptions.NoPlayerPriceThoughConsideredException;
import de.zabuza.beedlebot.exceptions.PageContentWrongFormatException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.service.routine.AnalyseResult;
import de.zabuza.beedlebot.service.routine.CentralTradersDepotNavigator;
import de.zabuza.beedlebot.store.EItemCategory;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.beedlebot.store.ItemPrice;
import de.zabuza.beedlebot.store.PlayerPrice;
import de.zabuza.beedlebot.store.Store;
import de.zabuza.sparkle.freewar.frames.EFrame;
import de.zabuza.sparkle.freewar.frames.IFrameManager;

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
	private final IFrameManager mFrameManager;
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	private final EItemCategory mItemCategory;
	private final ILogger mLogger;
	private final CentralTradersDepotNavigator mNavigator;
	private final AnalyseResult mResult;
	private final Store mStore;

	public AnalyseTask(final WebDriver driver, final IFrameManager frameManager, final AnalyseResult result,
			final EItemCategory itemCategory, final Store store, final CentralTradersDepotNavigator navigator) {
		this.mInterrupted = false;
		this.mDriver = driver;
		this.mFrameManager = frameManager;
		this.mResult = result;
		this.mItemCategory = itemCategory;
		this.mStore = store;
		this.mNavigator = navigator;
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
	public void start() throws ItemCategoryNotOpenedException {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Starting AnalyseTask");
		}

		// Open category
		final boolean wasClicked = this.mNavigator.openItemCategory(this.mItemCategory);

		if (!wasClicked) {
			this.mNavigator.exitMenu();
			throw new ItemCategoryNotOpenedException();
		}

		// Retrieve content
		// Ensure that we extract from the correct frame in case that another
		// thread might have changed the focus
		this.mFrameManager.switchToFrame(EFrame.MAIN);
		final String content = this.mDriver.getPageSource();

		// Process content
		processContent(content);

		// Finish the task
		this.mNavigator.exitMenu();
	}

	private void processContent(final String content) throws PageContentWrongFormatException,
			ItemLineWrongFormatException, NoPlayerPriceThoughConsideredException {
		// Strip everything between start and end needle
		final int startIndexRaw = content.indexOf(CONTENT_NEEDLE_START);
		final int startIndex = startIndexRaw + CONTENT_NEEDLE_START.length();
		final int endIndex = content.indexOf(CONTENT_NEEDLE_END, startIndex);

		if (startIndexRaw == -1 || endIndex == -1) {
			throw new PageContentWrongFormatException(content);
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
				throw new ItemLineWrongFormatException(itemContentLine);
			}

			final String itemName = itemContentLine.substring(itemNameStart + CONTENT_ITEM_NAME_START.length(),
					itemNameEnd);

			// Extract cost
			final int itemCostStart = itemContentLine.indexOf(CONTENT_ITEM_COST_START);
			final int itemCostEnd = itemContentLine.indexOf(CONTENT_ITEM_COST_END);

			if (itemCostStart == -1 || itemCostEnd == -1) {
				throw new ItemLineWrongFormatException(itemContentLine);
			}

			String itemCostText = itemContentLine.substring(itemCostStart + CONTENT_ITEM_COST_START.length(),
					itemCostEnd);
			// Remove thousand separator
			itemCostText = itemCostText.replaceAll("\\.", "");
			final int itemCost = Integer.parseInt(itemCostText);

			// Extract purchase anchor
			final int purchaseAnchorStart = itemContentLine.indexOf(CONTENT_BUY_ANCHOR_START);
			final int purchaseAnchorEnd = itemContentLine.indexOf(CONTENT_BUY_ANCHOR_END);

			if (purchaseAnchorStart == -1 || purchaseAnchorEnd == -1) {
				throw new ItemLineWrongFormatException(itemContentLine);
			}

			final String purchaseAnchor = itemContentLine
					.substring(purchaseAnchorStart + CONTENT_BUY_ANCHOR_START.length(), purchaseAnchorEnd);
			final String purchaseAnchorDecoded = purchaseAnchor.replaceAll("&amp;", "&");

			// Extract id from purchase anchor
			final int idStart = purchaseAnchor.indexOf(CONTENT_ID_START);

			if (idStart == -1) {
				throw new ItemLineWrongFormatException(itemContentLine);
			}

			final int id = Integer.parseInt(purchaseAnchor.substring(idStart + CONTENT_ID_START.length()));

			// Extract is magical state
			final boolean isMagical = itemContentLine.contains(CONTENT_IS_MAGICAL_PRESENCE);

			// Determine profit
			final ItemPrice itemPriceData = this.mStore.getItemPrice(itemName);
			final boolean isConsideredForShop = this.mStore.isItemConsideredForShop(itemName, itemCost, itemPriceData,
					this.mItemCategory);
			final int itemProfit;
			if (isConsideredForShop) {
				final int standardShopPrice = itemPriceData.getStandardShopPrice();
				itemProfit = Store.computeFullShopPrice(standardShopPrice) - itemCost;
			} else {
				final Optional<PlayerPrice> playerPrice = itemPriceData.getPlayerPrice();
				if (playerPrice.isPresent()) {
					itemProfit = playerPrice.get().getPrice() - itemCost;
				} else {
					throw new NoPlayerPriceThoughConsideredException(itemName);
				}
			}

			final Item item = new Item(itemName, itemCost, itemProfit, id, purchaseAnchorDecoded, isMagical,
					isConsideredForShop, itemPriceData, this.mItemCategory);

			// Add the item to the analyse result if accepted
			if (Store.isItemAccepted(item)) {
				this.mResult.add(item);
			}
		}
	}
}
