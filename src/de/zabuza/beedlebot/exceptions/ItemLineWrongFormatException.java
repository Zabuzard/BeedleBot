package de.zabuza.beedlebot.exceptions;

/**
 * Exception that is thrown whenever a raw text line containing item information
 * is in the wrong format and can not be parsed correctly.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ItemLineWrongFormatException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever the given
	 * raw text line containing item information is in the wrong format and can
	 * not be parsed correctly.
	 * 
	 * @param itemLine
	 *            The item line that is in the wrong format
	 */
	public ItemLineWrongFormatException(final String itemLine) {
		super(itemLine);
	}

}
