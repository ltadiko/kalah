package nl.backbase.game.kalah.domain.exception;

/**
 * thrown when no game found for the identifier or id
 */
public final class InvalidPitException extends RuntimeException {

    public InvalidPitException(String message) {
        super(message);
    }

}
