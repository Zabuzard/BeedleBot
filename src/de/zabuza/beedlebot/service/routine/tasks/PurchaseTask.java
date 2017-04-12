package de.zabuza.beedlebot.service.routine.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import de.zabuza.beedlebot.store.EItemCategory;
import de.zabuza.beedlebot.store.Item;
import de.zabuza.sparkle.freewar.IFreewarInstance;
import de.zabuza.sparkle.freewar.frames.EFrame;
import de.zabuza.sparkle.wait.EventQueueEmptyWait;

public final class PurchaseTask implements ITask {

	private static final String AMULET_CATEGORY_ANCHOR = "Kategorie: Halsschmuck";
	private static final String ATTACK_WEAPON_CATEGORY_ANCHOR = "Kategorie: Angriffswaffen";
	private static final String CONTINUE_ANCHOR = "Weiter";
	private static final String CSS_PURCHASE_ANCHOR_SELECTOR_PRE = "a[href=\"";
	private static final String CSS_PURCHASE_ANCHOR_SELECTOR_SUC = "\"]";
	private static final String DEFENSE_WEAPON_CATEGORY_ANCHOR = "Kategorie: Verteidigungswaffen";
	private static final String EXIT_MENU_ANCHOR = "Zur";
	private static final String MISCELLANEOUS_CATEGORY_ANCHOR = "Kategorie: Sonstiges";
	private static final String SPELL_CATEGORY_ANCHOR = "Kategorie: Anwendbare Items und Zauber";
	private final WebDriver mDriver;
	private final IFreewarInstance mInstance;
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	private final Item mItem;
	private final Map<EItemCategory, String> mItemCategoryToAnchorNeedle;
	private boolean mWasBought;

	public PurchaseTask(final IFreewarInstance instance, final WebDriver driver, final Item item) {
		mInstance = instance;
		mDriver = driver;
		mItem = item;
		mInterrupted = false;
		mWasBought = false;

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
		final String needle = mItemCategoryToAnchorNeedle.get(mItem.getItemCategory());
		final boolean wasCategoryClicked = mInstance.clickAnchorByContent(EFrame.MAIN, needle);

		// TODO Correct error handling and logging
		if (!wasCategoryClicked) {
			// TODO Remove debug print
			System.out.println("Category not clicked");
			exitMenu();
			return;
		}

		// Wait for click to get executed
		new EventQueueEmptyWait(mDriver).waitUntilCondition();

		// Click the purchase anchor
		final String selector = CSS_PURCHASE_ANCHOR_SELECTOR_PRE + mItem.getPurchaseAnchor()
				+ CSS_PURCHASE_ANCHOR_SELECTOR_SUC;
		final List<WebElement> elements = mDriver.findElements(By.cssSelector(selector));
		if (elements.isEmpty()) {
			// Item is not present anymore
			// TODO Correct error handling and logging
			// TODO Remove debug print
			System.out.println("Item not there anymore");
			exitMenu();
			return;
		}

		// Click the anchor
		elements.iterator().next().click();

		// Wait for click to get executed
		new EventQueueEmptyWait(mDriver).waitUntilCondition();

		// Click the continue anchor
		final boolean wasContinueClicked = mInstance.clickAnchorByContent(EFrame.MAIN, CONTINUE_ANCHOR);

		// TODO Correct error handling and logging
		if (!wasContinueClicked) {
			// TODO Remove debug print
			System.out.println("Continue not clicked");
			exitMenu();
			return;
		}

		// Wait for click to get executed
		new EventQueueEmptyWait(mDriver).waitUntilCondition();
		mWasBought = true;
	}

	public boolean wasBought() {
		return mWasBought;
	}

	private void exitMenu() {
		mInstance.clickAnchorByContent(EFrame.MAIN, EXIT_MENU_ANCHOR);

		// Wait for click to get executed
		new EventQueueEmptyWait(mDriver).waitUntilCondition();
	}
}
