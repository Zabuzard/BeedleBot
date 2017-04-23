package de.zabuza.beedlebot.databridge;

/**
 * All different states of the tool.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public enum EState {
	/**
	 * State in which the tool is actively performing actions.
	 */
	ACTIVE,
	/**
	 * State in which the tool is inactive, i.e. waiting for a start signal.
	 */
	INACTIVE,
	/**
	 * State in which the tool has a problem that needs to be resolved.
	 */
	PROBLEM,
	/**
	 * State in which the tool is in a standby mode, for example waiting for the
	 * arrival of new items at the central traders depot.
	 */
	STANDBY
}
