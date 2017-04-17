package de.zabuza.beedlebot.logging;

public final class LogMessage {
	private final ELogLevel mLogLevel;
	private final String mMessage;
	private final long mTimestamp;

	public LogMessage(final String message, final ELogLevel logLevel, final long timestamp) {
		mMessage = message;
		mLogLevel = logLevel;
		mTimestamp = timestamp;
	}

	public ELogLevel getLogLevel() {
		return mLogLevel;
	}

	public String getMessage() {
		return mMessage;
	}

	public long getTimestamp() {
		return mTimestamp;
	}
}
