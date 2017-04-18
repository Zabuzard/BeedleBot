package de.zabuza.beedlebot.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class LoggerUtil {
	public static final String getStackTrace(final Exception e) {
		final StringWriter target = new StringWriter();
		e.printStackTrace(new PrintWriter(target));
		return target.toString();
	}

	/**
	 * Utility class. No implementation.
	 */
	private LoggerUtil() {

	}
}
