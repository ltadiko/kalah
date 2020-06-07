package nl.backbase.game.kalah.domain.exception;

/**
 * thrown when no game found for the identifier or id
 */
public final class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(String message) {
        super(message);
    }

}
