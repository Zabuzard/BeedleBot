package de.zabuza.beedlebot.exceptions;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.html5.WebStorage;

/**
 * Exception that is thrown whenever a driver does not support
 * <a href= "https://www.w3schools.com/html/html5_webstorage.asp">HTML 5
 * Webstorage technology</a>. That is the case if the driver does not implement
 * {@link WebStorage} nor {@link JavascriptExecutor}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class DriverStorageUnsupportedException extends IllegalArgumentException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception. Indicates that the given driver
	 * does not support
	 * <a href= "https://www.w3schools.com/html/html5_webstorage.asp">HTML 5
	 * Webstorage technology</a>. That is the case if it does not implement
	 * {@link WebStorage} nor {@link JavascriptExecutor}.
	 * 
	 * @param driver
	 *            The driver that does not support the storage technology
	 */
	public DriverStorageUnsupportedException(final WebDriver driver) {
		super(driver.toString());
	}

}
