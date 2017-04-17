package de.zabuza.beedlebot.logging;

public interface ILogger {

	public void close();

	public void flush();

	public boolean isDebugEnabled();

	public void log(final String message, final ELogLevel level);

	public default void logDebug(final String message) {
		log(message, ELogLevel.DEBUG);
	}

	public default void logError(final String message) {
		log(message, ELogLevel.ERROR);
	}

	public default void logInfo(final String message) {
		log(message, ELogLevel.INFO);
	}

	public void setDebugEnabled(final boolean isDebugEnabled);
}
