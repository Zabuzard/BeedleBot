package de.zabuza.beedlebot.service.routine.tasks;

import java.util.Optional;

import org.openqa.selenium.WebDriver;

import de.zabuza.beedlebot.exceptions.ItemCategoryNotOpenedException;
import de.zabuza.beedlebot.exceptions.ItemLineWrongFormatException;
import de.zabuza.beedlebot.exceptions.NoPlayerPriceThoughConsideredException;
import de.zabuza.beedlebot.exceptions.PageContentWrongFormatException;
import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.service.routine.AnalyzeResult;
import de.zabuza.beedlebot.service.routine.CentralTradersDepotNavigator;
import de.zabuza.beedlebot.store.EItemCategory;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.beedlebot.store.ItemPrice;
import de.zabuza.beedlebot.store.PlayerPrice;
import de.zabuza.beedlebot.store.Store;
import de.zabuza.sparkle.freewar.frames.EFrame;
import de.zabuza.sparkle.freewar.frames.IFrameManager;

/**
 * Task that analyzes items at the central traders depot and creates
 * {@link AnalyzeResult}s which contains all items that should be bought because
 * they are accepted by {@link Store#isItemAcceptedForPurchase(Item)}. After
 * creation use {@link #start()}, the result will be put in-line into the
 * data-structure given at construction. After this task has ended it should not
 * be used anymore, instead create a new instance.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class AnalyzeTask implements ITask {
	/**
	 * The prefix of the anchor to buy an items.
	 */
	private static final String CONTENT_BUY_ANCHOR_PRE = "Gold <a href=\"";
	/**
	 * The suffix of the anchor to buy an items.
	 */
	private static final String CONTENT_BUY_ANCHOR_SUC = "\"> kaufen";
	/**
	 * The prefix of when the id of an item follows.
	 */
	private static final String CONTENT_ID_PRE = "mit_item=";
	/**
	 * The needle that represents if present whether an item is magic or not.
	 */
	private static final String CONTENT_IS_MAGICAL_PRESENCE = "class=\"itemmagic\"";
	/**
	 * The prefix of when the cost of an item follows.
	 */
	private static final String CONTENT_ITEM_COST_PRE = "für ";
	/**
	 * The suffix of when the cost of an item ends.
	 */
	private static final String CONTENT_ITEM_COST_SUC = " Gold";
	/**
	 * The prefix of when the name of an item follows.
	 */
	private static final String CONTENT_ITEM_NAME_PRE = "<b>";
	/**
	 * The suffix of when the name of an item ends.
	 */
	private static final String CONTENT_ITEM_NAME_SUC = "</b>";
	/**
	 * The pattern that splits lines in the raw content format.
	 */
	private static final String CONTENT_LINE_SPLIT_PATTERN = "(<br\\s*/>|<br>)";
	/**
	 * Symbol that validates if present whether a line has content to be parsed
	 * or not.
	 */
	private static final String CONTENT_LINE_VALIDATOR = "<b>";
	/**
	 * The prefix of when the content follows.
	 */
	private static final String CONTENT_NEEDLE_PRE = "Zurück";
	/**
	 * The suffix of when the content ends.
	 */
	private static final String CONTENT_NEEDLE_SUC = "Zurück";
	/**
	 * The driver to use for accessing browser contents.
	 */
	private final WebDriver mDriver;
	/**
	 * The frame manager to use for switching frame context in the game.
	 */
	private final IFrameManager mFrameManager;
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	/**
	 * The item category to be processed by this task.
	 */
	private final EItemCategory mItemCategory;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;
	/**
	 * The navigator to use for navigating menus at the central traders depot.
	 */
	private final CentralTradersDepotNavigator mNavigator;
	/**
	 * The result of this task.
	 */
	private final AnalyzeResult mResult;
	/**
	 * The store to use for accessing item price data.
	 */
	private final Store mStore;

	/**
	 * Creates a new analyze task that can be started with the {@link #start()}
	 * method. It analyzes items at the central traders depot and pushes the
	 * results into the given {@link AnalyzeResult} object that contains all
	 * items that should be bought because they are accepted by
	 * {@link Store#isItemAcceptedForPurchase(Item)}. After this task has ended
	 * it should not be used anymore, instead create a new instance.
	 * 
	 * @param driver
	 *            The driver to use for accessing browser contents
	 * @param frameManager
	 *            The frame manager to use for switching frame context of the
	 *            game
	 * @param result
	 *            The object to put the result of the analyze into
	 * @param itemCategory
	 *            The category of items to analyze
	 * @param store
	 *            The store to use for accessing item price data
	 * @param navigator
	 *            The navigator to use for navigating menus at the central
	 *            traders depot
	 */
	public AnalyzeTask(final WebDriver driver, final IFrameManager frameManager, final AnalyzeResult result,
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
			this.mLogger.logDebug("Starting AnalyzeTask");
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

	/**
	 * Processes the given raw page content and extracts and processes all
	 * contained items.
	 * 
	 * @param content
	 *            The raw page content to process
	 * @throws PageContentWrongFormatException
	 *             If the given raw page content is in a wrong format such that
	 *             it can not be parsed correctly
	 * @throws ItemLineWrongFormatException
	 *             If the given raw page content contains a line that validates
	 *             to a line which contains an item but it is in a wrong format
	 *             such that it can not be parsed correctly
	 * @throws NoPlayerPriceThoughConsideredException
	 *             If a processed item has no player price though it was
	 *             considered to have one
	 */
	private void processContent(final String content) throws PageContentWrongFormatException,
			ItemLineWrongFormatException, NoPlayerPriceThoughConsideredException {
		// Strip everything between start and end needle
		final int startIndexRaw = content.indexOf(CONTENT_NEEDLE_PRE);
		final int startIndex = startIndexRaw + CONTENT_NEEDLE_PRE.length();
		final int endIndex = content.indexOf(CONTENT_NEEDLE_SUC, startIndex);

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
			final int itemNameStart = itemContentLine.indexOf(CONTENT_ITEM_NAME_PRE);
			final int itemNameEnd = itemContentLine.indexOf(CONTENT_ITEM_NAME_SUC);

			if (itemNameStart == -1 || itemNameEnd == -1) {
				throw new ItemLineWrongFormatException(itemContentLine);
			}

			final String itemName = itemContentLine.substring(itemNameStart + CONTENT_ITEM_NAME_PRE.length(),
					itemNameEnd);

			// Extract cost
			final int itemCostStart = itemContentLine.indexOf(CONTENT_ITEM_COST_PRE);
			final int itemCostEnd = itemContentLine.indexOf(CONTENT_ITEM_COST_SUC);

			if (itemCostStart == -1 || itemCostEnd == -1) {
				throw new ItemLineWrongFormatException(itemContentLine);
			}

			String itemCostText = itemContentLine.substring(itemCostStart + CONTENT_ITEM_COST_PRE.length(),
					itemCostEnd);
			// Remove thousand separator
			itemCostText = itemCostText.replaceAll("\\.", "");
			final int itemCost = Integer.parseInt(itemCostText);

			// Extract purchase anchor
			final int purchaseAnchorStart = itemContentLine.indexOf(CONTENT_BUY_ANCHOR_PRE);
			final int purchaseAnchorEnd = itemContentLine.indexOf(CONTENT_BUY_ANCHOR_SUC);

			if (purchaseAnchorStart == -1 || purchaseAnchorEnd == -1) {
				throw new ItemLineWrongFormatException(itemContentLine);
			}

			final String purchaseAnchor = itemContentLine
					.substring(purchaseAnchorStart + CONTENT_BUY_ANCHOR_PRE.length(), purchaseAnchorEnd);
			final String purchaseAnchorDecoded = purchaseAnchor.replaceAll("&amp;", "&");

			// Extract id from purchase anchor
			final int idStart = purchaseAnchor.indexOf(CONTENT_ID_PRE);

			if (idStart == -1) {
				throw new ItemLineWrongFormatException(itemContentLine);
			}

			final int id = Integer.parseInt(purchaseAnchor.substring(idStart + CONTENT_ID_PRE.length()));

			// Extract is magical state
			final boolean isMagical = itemContentLine.contains(CONTENT_IS_MAGICAL_PRESENCE);

			// Determine profit
			final ItemPrice itemPriceData = this.mStore.getItemPrice(itemName);
			final boolean isConsideredForShop = this.mStore.isItemConsideredForShop(itemName, itemCost, itemPriceData);
			final int itemProfit;
			if (isConsideredForShop) {
				final int standardShopPrice = itemPriceData.getStandardShopPrice();
				itemProfit = Store.computeShopPriceWithDiscount(standardShopPrice) - itemCost;
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

			// Add the item to the analyze result if accepted
			if (Store.isItemAcceptedForPurchase(item)) {
				this.mResult.add(item);
			}
		}
	}
}
