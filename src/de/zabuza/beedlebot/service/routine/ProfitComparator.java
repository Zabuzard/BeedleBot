package de.zabuza.beedlebot.service.routine;

import java.util.Comparator;

import de.zabuza.beedlebot.store.Item;

/**
 * Compares items descending by their profit. That is an item with a higher
 * profit comes before an item with a lower profit.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ProfitComparator implements Comparator<Item> {

	/**
	 * Compares items descending by their profit. That is an item with a higher
	 * profit comes before an item with a lower profit.
	 * 
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(final Item first, final Item second) {
		return Integer.compare(second.getProfit(), first.getProfit());
	}

}
