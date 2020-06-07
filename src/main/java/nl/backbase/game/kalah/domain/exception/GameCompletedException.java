package nl.backbase.game.kalah.domain.exception;

/**
 * thrown when no game found for the identifier or id
 */
public final class GameCompletedException extends RuntimeException {

    public GameCompletedException(String message) {
        super(message);
    }

}
