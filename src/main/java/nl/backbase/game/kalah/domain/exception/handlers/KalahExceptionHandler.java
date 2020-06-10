package nl.backbase.game.kalah.domain.exception.handlers;

import nl.backbase.game.kalah.domain.exception.GameCompletedException;
import nl.backbase.game.kalah.domain.exception.GameNotFoundException;
import nl.backbase.game.kalah.domain.exception.InvalidGameException;
import nl.backbase.game.kalah.domain.exception.InvalidPitException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class KalahExceptionHandler {
    /**
     * Customize the response for GameNotFoundException.
     * <p>This method logs a warning and sets the "Allow" header
     *
     * @param ex      the exception
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @ExceptionHandler(GameNotFoundException.class)
    private ResponseEntity<Object> handleGameNotFoundException(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ErrorDetails.builder()
                .timestamp(new Date())
                .message(
                        ex.getMessage() != null && !ex.getMessage().isEmpty() ?
                                ex.getMessage() : "Game not found"
                )
                .build(),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND);
    }

    /**
     * Customize the response for InvalidPitException.
     * <p>This method logs a warning and sets the "Allow" header
     *
     * @param ex      the exception
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @ExceptionHandler(InvalidPitException.class)
    private ResponseEntity<Object> handleInvalidPitException(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ErrorDetails.builder()
                .timestamp(new Date())
                .message(
                        ex.getMessage() != null && !ex.getMessage().isEmpty() ?
                                ex.getMessage() : "Invalid Pit Number"
                )
                .build(),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Customize the response for InvalidGameException.
     * <p>This method logs a warning and sets the "Allow" header
     *
     * @param ex      the exception
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @ExceptionHandler(InvalidGameException.class)
    private ResponseEntity<Object> handleInvalidGameException(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ErrorDetails.builder()
                .timestamp(new Date())
                .message(
                        ex.getMessage() != null && !ex.getMessage().isEmpty() ?
                                ex.getMessage() : "Invalid Game ID"
                )
                .build(),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Customize the response for GameCompletedException.
     * <p>This method logs a warning and sets the "Allow" header
     *
     * @param ex      the exception
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @ExceptionHandler(GameCompletedException.class)
    private ResponseEntity<Object> handleGameCompletedException(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ErrorDetails.builder()
                .timestamp(new Date())
                .message(
                        ex.getMessage() != null && !ex.getMessage().isEmpty() ?
                                ex.getMessage() : "Invalid Game ID"
                )
                .build(),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

}
