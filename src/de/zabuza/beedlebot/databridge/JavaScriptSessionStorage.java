package de.zabuza.beedlebot.databridge;

import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.JavascriptExecutor;

/**
 * Session storage implementation that works on a {@link JavascriptExecutor}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class JavaScriptSessionStorage {
	/**
	 * Symbol that ends a Javascript command.
	 */
	private final static String SYMBOL_END_COMMAND = ";";
	/**
	 * Variable that represents the session storage in Javascript.
	 */
	private final static String VAR_SESSION_STORAGE = "window.sessionStorage";

	/**
	 * The Javascript executor to use for executing commands.
	 */
	private final JavascriptExecutor mExecutor;

	/**
	 * Creates a new instance which works on the given Javascript executor.
	 * 
	 * @param executor
	 *            The Javascript executor to use for executing commands
	 */
	public JavaScriptSessionStorage(final JavascriptExecutor executor) {
		this.mExecutor = executor;
	}

	public void clear() {
		final String script = String.format(VAR_SESSION_STORAGE + ".clear()" + SYMBOL_END_COMMAND);
		this.mExecutor.executeScript(script);
	}

	public String getItem(final String key) {
		final String script = String.format("return " + VAR_SESSION_STORAGE + ".getItem('%s')" + SYMBOL_END_COMMAND,
				key);
		final String item = (String) this.mExecutor.executeScript(script);
		return item;
	}

	public Set<String> keySet() {
		final Set<String> keys = new HashSet<>();

		final int size = size();
		for (int i = 0; i < size; i++) {
			final String getKeyNameScript = String
					.format("return " + VAR_SESSION_STORAGE + ".key(%d)" + SYMBOL_END_COMMAND, Integer.valueOf(i));
			final String key = (String) this.mExecutor.executeScript(getKeyNameScript);
			keys.add(key);
		}

		return keys;
	}

	public String removeItem(final String key) {
		final String script = String.format(VAR_SESSION_STORAGE + ".removeItem('%s')" + SYMBOL_END_COMMAND, key);
		this.mExecutor.executeScript(script);
		return null;
	}

	public void setItem(final String key, final String value) {
		final String script = String.format(VAR_SESSION_STORAGE + ".setItem('%s', '%s')" + SYMBOL_END_COMMAND, key,
				value);
		this.mExecutor.executeScript(script);
	}

	public int size() {
		final String script = "return " + VAR_SESSION_STORAGE + ".length" + SYMBOL_END_COMMAND;
		final long size = ((Long) this.mExecutor.executeScript(script)).longValue();
		return Math.toIntExact(size);
	}
}
