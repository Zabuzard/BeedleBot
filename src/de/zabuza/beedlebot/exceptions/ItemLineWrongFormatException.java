package de.zabuza.beedlebot.exceptions;

public final class ItemLineWrongFormatException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	public ItemLineWrongFormatException(final String itemLine) {
		super(itemLine);
	}

}
