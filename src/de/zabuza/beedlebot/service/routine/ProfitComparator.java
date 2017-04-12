package de.zabuza.beedlebot.service.routine;

import java.util.Comparator;

import de.zabuza.beedlebot.store.Item;

public final class ProfitComparator implements Comparator<Item> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final Item first, final Item second) {
		return Integer.compare(second.getProfit(), first.getProfit());
	}

}
