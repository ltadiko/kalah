package nl.backbase.game.kalah.service;

import nl.backbase.game.kalah.domain.Game;

public interface GameService {
    /**
     * function to create a new Kalah game resource
     *
     * @return returns new game
     */
    Game createGame();

    /**
     * function makes a move based on pit ID
     *
     * @param gameId Unique Id of the game
     * @param pitId  chosen Pit number to move coins by the player
     * @return returns game data after the move
     */
    Game makeMove(String gameId, int pitId);

    /**
     * function get game data based on unique game Id
     *
     * @param gameId unique identifier of a game
     * @return returns game data
     */
    Game getGame(String gameId);


}
