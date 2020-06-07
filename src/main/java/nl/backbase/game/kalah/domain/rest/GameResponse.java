package nl.backbase.game.kalah.domain.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.*;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameResponse {
    /**
     * unique identifier of a game
     */
    private String id;
    /**
     * link to the game created
     */
    private String uri;
    /**
     * Number of coins in each pit
     */
    private Map<Integer, Integer> status;
    /**
     * Message about which player should move
     */
    private String playerMessage;
}
