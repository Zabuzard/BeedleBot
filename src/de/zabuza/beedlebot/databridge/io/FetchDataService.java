package de.zabuza.beedlebot.databridge.io;

import de.zabuza.beedlebot.databridge.DataBridge;

/**
 * Service which fetches data from the tools web interface via a
 * {@link DataBridge}. Call {@link #update()} to fetch data from the bridge and
 * then use the given methods to access them.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class FetchDataService {

	/**
	 * The data bridge to use for accessing data.
	 */
	private final DataBridge mDataBridge;
	/**
	 * Whether the start signal flag is set or not. The value is not live, it
	 * can be updated via {@link #update()}.
	 */
	private boolean mIsStartSignalSet;
	/**
	 * Whether the stop signal flag is set or not. The value is not live, it can
	 * be updated via {@link #update()}.
	 */
	private boolean mIsStopSignalSet;

	/**
	 * Creates a new fetch data service which uses the given data bridge to
	 * access data. Call {@link #update()} to fetch data from the bridge and
	 * then use the given methods to access them.
	 * 
	 * @param dataBridge
	 *            The data bridge to use for accessing data
	 */
	public FetchDataService(final DataBridge dataBridge) {
		this.mDataBridge = dataBridge;
	}

	/**
	 * Clears the start signal flag by setting it to <tt>false</tt>.
	 */
	public void clearStartSignal() {
		this.mIsStartSignalSet = false;
		this.mDataBridge.clearStartSignal();
	}

	/**
	 * Clears the stop signal flag by setting it to <tt>false</tt>.
	 */
	public void clearStopSignal() {
		this.mIsStopSignalSet = false;
		this.mDataBridge.clearStopSignal();
	}

	/**
	 * Whether the start signal flag is set or not. The value is not live, it
	 * can be updated via {@link #update()}.
	 * 
	 * @return <tt>True</tt> if the start signal flag is set, <tt>false</tt> if
	 *         not.
	 */
	public boolean isStartSignalSet() {
		return this.mIsStartSignalSet;
	}

	/**
	 * Whether the stop signal flag is set or not. The value is not live, it can
	 * be updated via {@link #update()}.
	 * 
	 * @return <tt>True</tt> if the stop signal flag is set, <tt>false</tt> if
	 *         not.
	 */
	public boolean isStopSignalSet() {
		return this.mIsStopSignalSet;
	}

	/**
	 * Updates the data by fetching it from the data bridge.
	 */
	public void update() {
		// Fetch data
		setStartSignal(this.mDataBridge.isStartSignalSet());
		setStopSignal(this.mDataBridge.isStopSignalSet());
	}

	/**
	 * Sets the internal state of the start signal flag.
	 * 
	 * @param isStartSignalSet
	 *            The flag to set
	 */
	private void setStartSignal(final boolean isStartSignalSet) {
		this.mIsStartSignalSet = isStartSignalSet;
	}

	/**
	 * Sets the internal state of the stop signal flag.
	 * 
	 * @param isStopSignalSet
	 *            The flag to set
	 */
	private void setStopSignal(final boolean isStopSignalSet) {
		this.mIsStopSignalSet = isStopSignalSet;
	}

}
