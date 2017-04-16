package de.zabuza.beedlebot.exceptions;

import org.openqa.selenium.WebDriver;

public final class DriverStorageUnsupportedException extends IllegalArgumentException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public DriverStorageUnsupportedException(final WebDriver driver) {
		super(driver.toString());
	}

}
