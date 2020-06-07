package nl.backbase.game.kalah.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.backbase.game.kalah.domain.enums.GameStatus;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Builder
public class Game {
    @Id
    /**
     * unique id of the game
     */
    private String id;
    /**
     * contains all pits and house information
     * 7th pit and 14th pits are house coins count
     */
    private int[] pits;
    /**
     * initially the value is null
     * false when player one's turn to play
     * true when player two's turn to play
     */
    private boolean isSecondPlayerMove;
    /**
     * state of the game Created / In_Progress / FirstPlayerWon / SecondPlayerWon / Draw
     */
    private GameStatus gameStatus;
}
