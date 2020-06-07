package nl.backbase.game.kalah.service;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.game.kalah.domain.Game;
import nl.backbase.game.kalah.domain.enums.GameStatus;
import nl.backbase.game.kalah.domain.exception.GameCompletedException;
import nl.backbase.game.kalah.domain.exception.GameNotFoundException;
import nl.backbase.game.kalah.domain.exception.InvalidPitException;
import nl.backbase.game.kalah.repository.GameRepository;
import nl.backbase.game.kalah.utils.IdGeneratorUtil;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

    private GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Game createGame() {
        //initialize player
        Game game = Game.builder()
                .id(IdGeneratorUtil.generateGameId())
                .pits(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0})
                .isSecondPlayerMove(false)
                .gameStatus(GameStatus.CREATED)
                .build();
        gameRepository.save(game);
        return game;
    }


    @Override
    public Game makeMove(String gameId, int pitId) {
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new GameNotFoundException("GameId: " + gameId + " is not known while fetching game"));
        int[] pits = game.getPits();

        if (GameStatus.CREATED == game.getGameStatus()) {
            game.setGameStatus(GameStatus.IN_PROGRESS);
        } else if (GameStatus.IN_PROGRESS != game.getGameStatus()) {
            throw new GameCompletedException("Game is already completed." + game.getGameStatus());
        } else {
            if (game.isSecondPlayerMove() && pitId <= 7) {
                throw new InvalidPitException("Player one is not allowed to move");
            } else if (!game.isSecondPlayerMove() && pitId > 7) {
                throw new InvalidPitException("Player two is not allowed to move");
            }
        }


        int numberOfCoinsInPit = pits[pitId - 1];
        if (numberOfCoinsInPit <= 0) {
            throw new InvalidPitException("No coins are present in selected pit.Please choose another pit");
        }

        for (int i = 1; i <= numberOfCoinsInPit; i++) {
            int nextPit = pitId + i > 14 ? pitId + i - 14 : pitId + i;
            //  No stones are put in the opponent's' Kalah.()
            boolean isNextPitOpponentKalah = (pitId < 7 && nextPit == 14) || (pitId > 7 && nextPit == 7);
            if (isNextPitOpponentKalah) {
                numberOfCoinsInPit++;
            } else {
                pits[nextPit - 1] += 1;
                //the last coin in move
                if (i == numberOfCoinsInPit) {
                    pits[pitId - 1] = 0;
                    /* When the last stone lands in an own empty pit, the player captures this stone and all stones in the opposite pit (the other players' pit) and puts them in his own Kalah  */
                    handleLastStoneLandsInOwnEmptyPit(game.isSecondPlayerMove(), pits, nextPit);
                    if (isGameCompleted(pits)) {
                        pits = moveRemainingCoins(pits);
                        game.setGameStatus(getGameStatus(pits));
                        log.debug("Game is completed with game status {}", game.getGameStatus());
                    } else {
                        if (nextPit <= 7) {
                            game.setSecondPlayerMove(false);
                            log.debug("First player should move the coins manually");
                        } else {
                            game.setSecondPlayerMove(true);
                            log.debug("Second player should move the coins manually");
                        }
                    }
                }
            }
        }

        game.setPits(pits);
        gameRepository.save(game);
        return game;
    }

    /**
     * This method checks last stone lands in an own empty pit
     * then the player captures this stone and all stones in the opposite pit (the other players' pit) and puts them in his own Kalah
     *
     * @param isSecondPlayerMove is the pit moved by second player.
     * @param pits               current pit values
     * @param nextPit            nextpit number
     */
    private void handleLastStoneLandsInOwnEmptyPit(boolean isSecondPlayerMove, int[] pits, int nextPit) {
        if (pits[nextPit - 1] == 1 && nextPit != 7 && nextPit != 14) {
            //check if it is own

            if (!isSecondPlayerMove && nextPit < 7) {
                //move all coins from opposite pit
                int oppositePitNumber = nextPit + 7;
                moveCoinsFromOppositePitAndPitToKalah(pits, 7, nextPit, oppositePitNumber);
            }
            if (isSecondPlayerMove && nextPit > 7) {
                //move all coins from opposite pit
                int oppositePitNumber = nextPit - 7;
                moveCoinsFromOppositePitAndPitToKalah(pits, 14, nextPit, oppositePitNumber);
            }
        }
    }

    /*
    When the last stone lands in an own empty pit, the player captures this stone and all stones in the opposite pit (the
    other players' pit) and puts them in his own Kalah.
     */
    private void moveCoinsFromOppositePitAndPitToKalah(int[] pits, int kalahNumber, int nextPit, int oppositePitNumber) {
        pits[kalahNumber - 1] += pits[nextPit - 1] + pits[oppositePitNumber - 1];
        pits[nextPit - 1] = 0;
        pits[oppositePitNumber - 1] = 0;
        log.debug("all coins from opposite pit {} are moved to pit {}", oppositePitNumber, nextPit);
    }

    @Override
    public Game getGame(String gameId) {
        return gameRepository.findById(gameId).orElseThrow(
                () -> new GameNotFoundException("GameId: " + gameId + " is not known while fetching game"));
    }


    private boolean isGameCompleted(int[] pits) {
        int playerOneCoinsInPit = IntStream.range(0, 6).map(pitNumber -> pits[pitNumber]).sum();
        int playerTwoCoinsInPit = IntStream.range(7, 13).map(pitNumber -> pits[pitNumber]).sum();
        if (playerOneCoinsInPit == 0 || playerTwoCoinsInPit == 0) {
            log.debug("Game is completed.coins in player one pits are {} and coins in player two pits are {}", playerOneCoinsInPit, playerTwoCoinsInPit);
            return true;
        }
        return false;
    }

    private int[] moveRemainingCoins(int[] pits) {
        //The player who still has stones in his/her pits keeps them and puts them in his/hers Kalah.
        pits[13] += IntStream.range(7, 13).map(pitNumber -> pits[pitNumber]).sum();
        pits[7] = pits[8] = pits[9] = pits[10] = pits[11] = pits[12] = 0;
        //The player who still has stones in his/her pits keeps them and puts them in his/hers Kalah.
        pits[6] += IntStream.range(0, 6).map(pitNumber -> pits[pitNumber]).sum();
        pits[0] = pits[1] = pits[2] = pits[3] = pits[4] = pits[5] = 0;

        return pits;
    }

    private GameStatus getGameStatus(int[] pits) {
        if (pits[6] > pits[13]) {
            return GameStatus.FIRSTPLAYERWON;
        } else if (pits[6] < pits[13]) {
            return GameStatus.SECONDPLAYERWON;
        } else {
            return GameStatus.DRAW;
        }
    }
}
