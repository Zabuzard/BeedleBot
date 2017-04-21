package de.zabuza.beedlebot.logging;

public final class LogMessage {
	private final ELogLevel mLogLevel;
	private final String mMessage;
	private final long mTimestamp;

	public LogMessage(final String message, final ELogLevel logLevel, final long timestamp) {
		this.mMessage = message;
		this.mLogLevel = logLevel;
		this.mTimestamp = timestamp;
	}

	public ELogLevel getLogLevel() {
		return this.mLogLevel;
	}

	public String getMessage() {
		return this.mMessage;
	}

	public long getTimestamp() {
		return this.mTimestamp;
	}
}
