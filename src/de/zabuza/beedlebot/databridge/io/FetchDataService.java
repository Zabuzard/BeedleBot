package de.zabuza.beedlebot.databridge.io;

import de.zabuza.beedlebot.databridge.DataBridge;

public final class FetchDataService {

	private final DataBridge mDataBridge;
	private boolean mIsStartSignalSet;
	private boolean mIsStopSignalSet;

	public FetchDataService(final DataBridge dataBridge) {
		mDataBridge = dataBridge;
	}

	public void update() {
		// Fetch data
		setStartSignal(mDataBridge.isStartSignalSet());
		setStopSignal(mDataBridge.isStopSignalSet());
	}

	private void setStartSignal(final boolean isStartSignalSet) {
		mIsStartSignalSet = isStartSignalSet;
	}

	public void clearStartSignal() {
		mIsStartSignalSet = false;
		mDataBridge.clearStartSignal();
	}

	public void clearStopSignal() {
		mIsStopSignalSet = false;
		mDataBridge.clearStopSignal();
	}

	private void setStopSignal(final boolean isStopSignalSet) {
		mIsStopSignalSet = isStopSignalSet;
	}

	public boolean isStartSignalSet() {
		return mIsStartSignalSet;
	}

	public boolean isStopSignalSet() {
		return mIsStopSignalSet;
	}

}
