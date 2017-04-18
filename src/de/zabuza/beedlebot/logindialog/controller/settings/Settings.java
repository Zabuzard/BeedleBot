package de.zabuza.beedlebot.logindialog.controller.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.beedlebot.logging.LoggerUtil;

/**
 * Class for the tool settings.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 * 
 */
public final class Settings {
	/**
	 * Comment for the configuration file.
	 */
	private static final String FILE_COMMENT = "Configuration settings for BeedleBot.";
	/**
	 * File path of the settings.
	 */
	private static final String FILEPATH = "config.ini";
	/**
	 * Logger to use for logging.
	 */
	private final ILogger mLogger;
	/**
	 * Properties object which holds the saved settings.
	 */
	private final Properties mProperties;

	/**
	 * Create a new settings object.
	 */
	public Settings() {
		mProperties = new Properties();
		mLogger = LoggerFactory.getLogger();
	}

	/**
	 * Loads settings of the saved file and applies the properties to the
	 * provider settings.
	 * 
	 * @param provider
	 *            Provider which settings will be affected
	 */
	public final void loadSettings(final ISettingsProvider provider) {
		mLogger.logInfo("Loading settings");
		try {
			try {
				mProperties.load(new FileInputStream(FILEPATH));
			} catch (final FileNotFoundException e) {
				mLogger.logInfo("Creating settings file");
				saveSettings(provider);
				mProperties.load(new FileInputStream(FILEPATH));
			}

			// Fetch and set every saved setting
			for (Entry<Object, Object> entry : mProperties.entrySet()) {
				provider.setSetting((String) entry.getKey(), (String) entry.getValue());
			}
		} catch (final IOException e) {
			// Log the error but continue
			mLogger.logError("Error while loading settings: " + LoggerUtil.getStackTrace(e));
		}
	}

	/**
	 * Saves the current settings of the provider in a file.
	 * 
	 * @param provider
	 *            Provider which settings will be affected
	 */
	public final void saveSettings(final ISettingsProvider provider) {
		mLogger.logInfo("Saving settings");

		FileOutputStream target = null;
		try {
			// Fetch and put every setting
			for (Entry<String, String> entry : provider.getAllSettings().entrySet()) {
				mProperties.put(entry.getKey(), entry.getValue());
			}

			// Save the settings
			target = new FileOutputStream(new File(FILEPATH));
			mProperties.store(target, FILE_COMMENT);
		} catch (final IOException e) {
			// Log the error but continue
			mLogger.logError("Error while saving settings: " + LoggerUtil.getStackTrace(e));
		} finally {
			if (target != null) {
				try {
					target.close();
				} catch (final IOException e) {
					// Log the error but continue
					mLogger.logError("Error while closing settings file for saving: " + LoggerUtil.getStackTrace(e));
				}
			}
		}
	}
}