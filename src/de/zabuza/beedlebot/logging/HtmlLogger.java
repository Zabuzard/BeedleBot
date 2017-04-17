package de.zabuza.beedlebot.logging;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import de.zabuza.beedlebot.exceptions.LoggerCanNotReadException;
import de.zabuza.beedlebot.exceptions.LoggerCanNotWriteException;

public final class HtmlLogger implements ILogger {

	private static final String CLASS_LOG_CONTENT = "logContent";
	private static final String CLASS_LOG_DEBUG = "debugLog";
	private static final String CLASS_LOG_ERROR = "errorLog";
	private static final String CLASS_LOG_INFO = "infoLog";
	private static final String CLASS_LOG_TIMESTAMP = "logTimestamp";
	private static final String FILEPATH = "log.html";
	private static final int LOG_EVERY = 200;
	private static final int LOG_MESSAGES_MAX = 10_000;
	private static final String MESSAGE_SEPARATOR = "<br />";
	private static final String NO_LOG_MESSAGE_INDICATOR = "<!--NO_MESSAGE-->";
	private static final String TITLE = "BeedleBot Log";

	private String mHeadMessage;
	private boolean mIsDebugEnabled;
	private final String mLineSeparator;
	private final Queue<LogMessage> mMessageBuffer;
	private String mTailMessage;

	public HtmlLogger() {
		this(false);
	}

	public HtmlLogger(final boolean isDebugEnabled) {
		mLineSeparator = System.lineSeparator();
		mHeadMessage = null;
		mTailMessage = null;
		mIsDebugEnabled = isDebugEnabled;

		mMessageBuffer = new LinkedList<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.logging.ILogger#close()
	 */
	@Override
	public void close() {
		flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.logging.ILogger#flush()
	 */
	@Override
	public void flush() {
		if (mMessageBuffer.isEmpty()) {
			return;
		}

		final Queue<String> loggedMessages = readLoggedMessages();
		FileWriter writer = null;
		try {
			writer = new FileWriter(FILEPATH, false);
			writer.write(getHeadMessage());
			writer.write(mLineSeparator);

			final Queue<String> messagesToWrite = mixBufferIntoMessages(loggedMessages, mMessageBuffer);
			mMessageBuffer.clear();
			while (!messagesToWrite.isEmpty()) {
				final String message = messagesToWrite.poll();
				writer.write(message);
				if (!message.endsWith(mLineSeparator)) {
					writer.write(mLineSeparator);
				}
			}

			writer.write(getTailMessage());
			writer.flush();
		} catch (final IOException e) {
			throw new LoggerCanNotWriteException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (final IOException e) {
					throw new LoggerCanNotWriteException(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.logging.ILogger#isDebugEnabled()
	 */
	@Override
	public boolean isDebugEnabled() {
		return mIsDebugEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.logging.ILogger#log(java.lang.String,
	 * de.zabuza.beedlebot.logging.ELogLevel)
	 */
	@Override
	public void log(final String message, final ELogLevel level) {
		// Do not log debug if not enabled
		if (level == ELogLevel.DEBUG && !isDebugEnabled()) {
			return;
		}

		final long timestampNow = System.currentTimeMillis();
		final LogMessage logMessage = new LogMessage(message, level, timestampNow);
		mMessageBuffer.add(logMessage);

		if (mMessageBuffer.size() >= LOG_EVERY) {
			flush();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.logging.ILogger#setDebugEnabled(boolean)
	 */
	@Override
	public void setDebugEnabled(final boolean isDebugEnabled) {
		mIsDebugEnabled = isDebugEnabled;
	}

	private String escapeHtml(final String text) {
		final String lineSeparator = System.lineSeparator();
		String result = text.replaceAll("\"", "&quot;");
		result = result.replaceAll("&", "&amp;");
		result = result.replaceAll("<", "&lt;");
		result = result.replaceAll(">", "&gt;");
		result = result.replaceAll(lineSeparator, "<br />");
		return result;
	}

	private String getHeadMessage() {
		if (mHeadMessage != null) {
			return mHeadMessage;
		}

		final StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>");
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<title>").append(TITLE).append("</title>");
		sb.append("<style>");
		sb.append(".").append(CLASS_LOG_INFO).append(" .").append(CLASS_LOG_TIMESTAMP).append(" { }");
		sb.append(".").append(CLASS_LOG_DEBUG).append(" .").append(CLASS_LOG_TIMESTAMP)
				.append(" { background-color: #CFE4F3; }");
		sb.append(".").append(CLASS_LOG_ERROR).append(" .").append(CLASS_LOG_TIMESTAMP)
				.append(" { background-color: #F2CEC8; }");
		sb.append(".").append(CLASS_LOG_CONTENT).append(" { margin-left: 10px;}");
		sb.append("</style>");
		sb.append("</head><body>");
		sb.append(NO_LOG_MESSAGE_INDICATOR);

		mHeadMessage = sb.toString();
		return mHeadMessage;
	}

	private String getTailMessage() {
		if (mTailMessage != null) {
			return mTailMessage;
		}

		final StringBuilder sb = new StringBuilder();
		sb.append("</body>");
		sb.append("</html>");
		sb.append(NO_LOG_MESSAGE_INDICATOR);

		mTailMessage = sb.toString();
		return mTailMessage;
	}

	private String messageToHtml(final LogMessage message) {
		final String content = message.getMessage();
		final ELogLevel level = message.getLogLevel();
		final long timestamp = message.getTimestamp();
		final Date date = new Date(timestamp);
		final String timestampFormat = DateFormat.getDateTimeInstance().format(date);

		final String cssLogClass;
		if (level == ELogLevel.INFO) {
			cssLogClass = CLASS_LOG_INFO;
		} else if (level == ELogLevel.DEBUG) {
			cssLogClass = CLASS_LOG_DEBUG;
		} else if (level == ELogLevel.ERROR) {
			cssLogClass = CLASS_LOG_ERROR;
		} else {
			throw new AssertionError();
		}

		final StringBuilder sb = new StringBuilder();
		sb.append("<span class=\"").append(cssLogClass).append("\">");

		sb.append("<span class=\"").append(CLASS_LOG_TIMESTAMP).append("\">");
		sb.append(timestampFormat);
		sb.append(":</span>");

		sb.append("<span class=\"").append(CLASS_LOG_CONTENT).append("\">");
		sb.append(escapeHtml(content));
		sb.append("</span>");

		sb.append("</span>");
		sb.append(MESSAGE_SEPARATOR);

		return sb.toString();
	}

	private Queue<String> mixBufferIntoMessages(final Queue<String> loggedMessages,
			final Queue<LogMessage> bufferedMessages) {
		for (final LogMessage logMessage : bufferedMessages) {
			loggedMessages.add(messageToHtml(logMessage));
		}

		while (loggedMessages.size() > LOG_MESSAGES_MAX) {
			loggedMessages.remove();
		}
		return loggedMessages;
	}

	private Queue<String> readLoggedMessages() {
		final Queue<String> messages = new LinkedList<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(FILEPATH));
			while (br.ready()) {
				final String line = br.readLine();
				if (line == null) {
					break;
				}

				if (line.endsWith(NO_LOG_MESSAGE_INDICATOR)) {
					continue;
				}

				messages.add(line);
			}
		} catch (final FileNotFoundException e) {
			// There are no messages
			return messages;
		} catch (final IOException e) {
			throw new LoggerCanNotReadException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
					throw new LoggerCanNotReadException(e);
				}
			}
		}

		return messages;
	}

}
