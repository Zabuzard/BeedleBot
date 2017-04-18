package de.zabuza.beedlebot.databridge;

import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.html5.SessionStorage;

public final class JavaScriptSessionStorage implements SessionStorage {
	private final static String SYMBOL_END_COMMAND = ";";
	private final static String VAR_SESSION_STORAGE = "window.sessionStorage";

	private final JavascriptExecutor mExecutor;

	public JavaScriptSessionStorage(final JavascriptExecutor executor) {
		mExecutor = executor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openqa.selenium.html5.SessionStorage#clear()
	 */
	@Override
	public void clear() {
		final String script = String.format(VAR_SESSION_STORAGE + ".clear()" + SYMBOL_END_COMMAND);
		mExecutor.executeScript(script);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openqa.selenium.html5.SessionStorage#getItem(java.lang.String)
	 */
	@Override
	public String getItem(final String key) {
		final String script = String.format("return " + VAR_SESSION_STORAGE + ".getItem('%s')" + SYMBOL_END_COMMAND,
				key);
		final String item = (String) mExecutor.executeScript(script);
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openqa.selenium.html5.SessionStorage#keySet()
	 */
	@Override
	public Set<String> keySet() {
		final Set<String> keys = new HashSet<>();

		final int size = size();
		for (int i = 0; i < size; i++) {
			final String getKeyNameScript = String
					.format("return " + VAR_SESSION_STORAGE + ".key(%d)" + SYMBOL_END_COMMAND, i);
			final String key = (String) mExecutor.executeScript(getKeyNameScript);
			keys.add(key);
		}

		return keys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openqa.selenium.html5.SessionStorage#removeItem(java.lang.String)
	 */
	@Override
	public String removeItem(final String key) {
		final String script = String.format(VAR_SESSION_STORAGE + ".removeItem('%s')" + SYMBOL_END_COMMAND, key);
		mExecutor.executeScript(script);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openqa.selenium.html5.SessionStorage#setItem(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setItem(final String key, final String value) {
		final String script = String.format(VAR_SESSION_STORAGE + ".setItem('%s', '%s')" + SYMBOL_END_COMMAND, key,
				value);
		mExecutor.executeScript(script);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openqa.selenium.html5.SessionStorage#size()
	 */
	@Override
	public int size() {
		final String script = "return " + VAR_SESSION_STORAGE + ".length" + SYMBOL_END_COMMAND;
		final long size = (long) mExecutor.executeScript(script);
		return Math.toIntExact(size);
	}

}
