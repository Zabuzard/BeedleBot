package de.zabuza.beedlebot.logging;

public final class LoggerFactory {

	private static ILogger loggerInstance = null;

	public static ILogger getLogger() {
		if (loggerInstance == null) {
			loggerInstance = new HtmlLogger();
		}

		return loggerInstance;
	}

	public static void main(final String[] args) {
		final ILogger logger = LoggerFactory.getLogger();

		for (int i = 1; i <= 250; i++) {
			logger.logInfo("Nachricht #" + i);
		}

		logger.close();
	}
}
