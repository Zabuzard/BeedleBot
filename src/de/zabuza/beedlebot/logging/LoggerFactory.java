package de.zabuza.beedlebot.logging;

public final class LoggerFactory {

	private static ILogger loggerInstance = null;

	public static ILogger getLogger() {
		if (loggerInstance == null) {
			loggerInstance = new HtmlLogger();
		}

		return loggerInstance;
	}
}
