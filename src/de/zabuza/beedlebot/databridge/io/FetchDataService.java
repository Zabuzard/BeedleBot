package de.zabuza.beedlebot.databridge.io;

import de.zabuza.beedlebot.databridge.DataBridge;

public final class FetchDataService {

	private final DataBridge mDataBridge;
	private boolean mIsStartSignalSet;
	private boolean mIsStopSignalSet;

	public FetchDataService(final DataBridge dataBridge) {
		this.mDataBridge = dataBridge;
	}

	public void clearStartSignal() {
		this.mIsStartSignalSet = false;
		this.mDataBridge.clearStartSignal();
	}

	public void clearStopSignal() {
		this.mIsStopSignalSet = false;
		this.mDataBridge.clearStopSignal();
	}

	public boolean isStartSignalSet() {
		return this.mIsStartSignalSet;
	}

	public boolean isStopSignalSet() {
		return this.mIsStopSignalSet;
	}

	public void update() {
		// Fetch data
		setStartSignal(this.mDataBridge.isStartSignalSet());
		setStopSignal(this.mDataBridge.isStopSignalSet());
	}

	private void setStartSignal(final boolean isStartSignalSet) {
		this.mIsStartSignalSet = isStartSignalSet;
	}

	private void setStopSignal(final boolean isStopSignalSet) {
		this.mIsStopSignalSet = isStopSignalSet;
	}

}
