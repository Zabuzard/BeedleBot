package de.zabuza.beedlebot.service.routine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.store.EItemCategory;
import de.zabuza.sparkle.freewar.IFreewarInstance;
import de.zabuza.sparkle.freewar.chat.IChat;
import de.zabuza.sparkle.freewar.frames.EFrame;
import de.zabuza.sparkle.freewar.frames.IFrameManager;
import de.zabuza.sparkle.wait.EventQueueEmptyWait;
import de.zabuza.sparkle.wait.LinkTextPresenceWait;

/**
 * Navigator object that provides various utility methods for interacting with
 * the central traders depot.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class CentralTradersDepotNavigator {
	/**
	 * Partial link text of the anchor that opens the amulet item category.
	 */
	private static final String AMULET_CATEGORY_ANCHOR = "Kategorie: Halsschmuck";
	/**
	 * Partial link text of the anchor that opens the attack weapon item
	 * category.
	 */
	private static final String ATTACK_WEAPON_CATEGORY_ANCHOR = "Kategorie: Angriffswaffen";
	/**
	 * Partial link text of the anchor to continue with the purchase dialog.
	 */
	private static final String CONTINUE_ANCHOR = "Weiter";
	/**
	 * CSS prefix of the anchor to purchase an item.
	 */
	private static final String CSS_PURCHASE_ANCHOR_SELECTOR_PRE = "a[href=\"";
	/**
	 * CSS suffix of the anchor to purchase an item.
	 */
	private static final String CSS_PURCHASE_ANCHOR_SELECTOR_SUC = "\"]";
	/**
	 * Partial link text of the anchor that opens the defense weapon item
	 * category.
	 */
	private static final String DEFENSE_WEAPON_CATEGORY_ANCHOR = "Kategorie: Verteidigungswaffen";
	/**
	 * Partial link text of the anchor that exits the purchase menu.
	 */
	private static final String EXIT_MENU_ANCHOR = "Zur";
	/**
	 * Partial link text of the anchor that opens the miscellaneous item
	 * category.
	 */
	private static final String MISCELLANEOUS_CATEGORY_ANCHOR = "Kategorie: Sonstiges";
	/**
	 * Partial link text of the anchor that opens the spell item category.
	 */
	private static final String SPELL_CATEGORY_ANCHOR = "Kategorie: Anwendbare Items und Zauber";
	/**
	 * The chat object to use for focusing the chat input.
	 */
	private final IChat mChat;
	/**
	 * The driver to use for accessing browser contents.
	 */
	private final WebDriver mDriver;
	/**
	 * The frame manager to use for switching the frame context of the game.
	 */
	private final IFrameManager mFrameManager;
	/**
	 * The instance to use for accessing the games contents.
	 */
	private final IFreewarInstance mInstance;
	/**
	 * Data-structure that maps item categories to their anchor needles.
	 */
	private final Map<EItemCategory, String> mItemCategoryToAnchorNeedle;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;

	/**
	 * Creates a new central traders depot navigator that operates on the given
	 * data.
	 * 
	 * @param instance
	 *            The instance to use for accessing the games contents.
	 * @param driver
	 *            The driver to use for accessing browser contents.
	 */
	public CentralTradersDepotNavigator(final IFreewarInstance instance, final WebDriver driver) {
		this.mInstance = instance;
		this.mChat = instance.getChat();
		this.mFrameManager = instance.getFrameManager();
		this.mDriver = driver;
		this.mLogger = LoggerFactory.getLogger();

		this.mItemCategoryToAnchorNeedle = new HashMap<>();
		this.mItemCategoryToAnchorNeedle.put(EItemCategory.ATTACK_WEAPON, ATTACK_WEAPON_CATEGORY_ANCHOR);
		this.mItemCategoryToAnchorNeedle.put(EItemCategory.DEFENSE_WEAPON, DEFENSE_WEAPON_CATEGORY_ANCHOR);
		this.mItemCategoryToAnchorNeedle.put(EItemCategory.AMULET, AMULET_CATEGORY_ANCHOR);
		this.mItemCategoryToAnchorNeedle.put(EItemCategory.SPELL, SPELL_CATEGORY_ANCHOR);
		this.mItemCategoryToAnchorNeedle.put(EItemCategory.MISCELLANEOUS, MISCELLANEOUS_CATEGORY_ANCHOR);
	}

	/**
	 * Tries to exit the central traders depot menu and focuses the chat input
	 * afterwards.
	 * 
	 * @return <tt>True</tt> if the menu was closed, <tt>false</tt> if that was
	 *         not possible
	 */
	public boolean exitMenu() {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Exiting central traders depot menu");
		}

		final boolean wasClicked = this.mInstance.clickAnchorByContent(EFrame.MAIN, EXIT_MENU_ANCHOR);

		// Wait for click to get executed
		new EventQueueEmptyWait(this.mDriver).waitUntilCondition();

		// Focus chat input in case user was typing before
		this.mChat.focusChatInput();

		return wasClicked;
	}

	/**
	 * Tries to exit the central traders depot purchased dialog and focuses the
	 * chat input afterwards.
	 * 
	 * @return <tt>True</tt> if the menu was closed, <tt>false</tt> if that was
	 *         not possible
	 */
	public boolean exitPurchasedDialog() {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Exiting central traders depot purchased dialog");
		}

		// Wait for dialog to appear
		this.mFrameManager.switchToFrame(EFrame.MAIN);
		new LinkTextPresenceWait(this.mDriver, CONTINUE_ANCHOR).waitUntilCondition();
		final boolean wasClicked = this.mInstance.clickAnchorByContent(EFrame.MAIN, CONTINUE_ANCHOR);

		// Wait for click to get executed
		new EventQueueEmptyWait(this.mDriver).waitUntilCondition();

		// Focus chat input in case user was typing before
		this.mChat.focusChatInput();

		return wasClicked;
	}

	/**
	 * Tries to open the purchase menu for the given item category and focuses
	 * the chat input afterwards.
	 * 
	 * @param category
	 *            The item category to open its purchase menu
	 * @return <tt>True</tt> if the menu was opened, <tt>false</tt> if that was
	 *         not possible
	 */
	public boolean openItemCategory(final EItemCategory category) {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Opening central traders depot item category: " + category);
		}

		final String needle = this.mItemCategoryToAnchorNeedle.get(category);
		final boolean wasClicked = this.mInstance.clickAnchorByContent(EFrame.MAIN, needle);

		// Wait for click to get executed
		new EventQueueEmptyWait(this.mDriver).waitUntilCondition();

		// Focus chat input in case user was typing before
		this.mChat.focusChatInput();

		return wasClicked;
	}

	/**
	 * Tries to purchase the item with the given anchor from the central traders
	 * depot and focuses the chat input afterwards. Prior to this the correct
	 * item category purchase menu needs to be opened with
	 * {@link #openItemCategory(EItemCategory)}.
	 * 
	 * @param purchaseAnchor
	 *            The anchor of the item to purchase
	 * @return <tt>True</tt> if the item was purchased, <tt>false</tt> if that
	 *         was not possible
	 */
	public boolean purchaseItem(final String purchaseAnchor) {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Purchasing central traders depot item: " + purchaseAnchor);
		}

		this.mFrameManager.switchToFrame(EFrame.MAIN);
		final String selector = CSS_PURCHASE_ANCHOR_SELECTOR_PRE + purchaseAnchor + CSS_PURCHASE_ANCHOR_SELECTOR_SUC;
		final List<WebElement> elements = this.mDriver.findElements(By.cssSelector(selector));
		if (elements.isEmpty()) {
			return false;
		}

		// Click the anchor
		elements.iterator().next().click();

		// Wait for click to get executed
		new EventQueueEmptyWait(this.mDriver).waitUntilCondition();

		// Focus chat input in case user was typing before
		this.mChat.focusChatInput();

		return true;
	}
}
