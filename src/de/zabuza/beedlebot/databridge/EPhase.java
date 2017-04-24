package de.zabuza.beedlebot.databridge;

/**
 * All different phases of the tool.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public enum EPhase {
	/**
	 * Phase in which the tool analyzes all items of the central traders depot.
	 */
	ANALYZE,
	/**
	 * Phase in which the tool awaits a new item delivery at the central traders
	 * depot.
	 */
	AWAITING_DELIVERY,
	/**
	 * Phase in which the tool purchases a set item from the central traders
	 * depot.
	 */
	PURCHASE,
	/**
	 * Phase in which the tool waits until it can purchase an item from the
	 * central traders depot.
	 */
	WAIT
}
