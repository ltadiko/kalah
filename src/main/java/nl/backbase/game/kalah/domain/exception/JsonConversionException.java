package nl.backbase.game.kalah.domain.exception;

/**
 * A wrapper for exceptions occurring while converting to JSON
 */
public final class JsonConversionException extends RuntimeException {

	/**
	 * Handles all exceptions that occur while converting to JSON
	 *
	 * @param message: custom message based on exception
	 * @param cause: wrapped exception
	 */
	public JsonConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}
