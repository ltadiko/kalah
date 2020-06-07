package nl.backbase.game.kalah.domain.exception;

/**
 * thrown when no game found for the identifier or id
 */
public final class InvalidGameException extends RuntimeException {

    public InvalidGameException(String message) {
        super(message);
    }

}
