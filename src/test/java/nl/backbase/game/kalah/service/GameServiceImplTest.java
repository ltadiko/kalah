package nl.backbase.game.kalah.service;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import nl.backbase.game.kalah.domain.Game;
import nl.backbase.game.kalah.domain.enums.GameStatus;
import nl.backbase.game.kalah.domain.exception.GameCompletedException;
import nl.backbase.game.kalah.domain.exception.GameNotFoundException;
import nl.backbase.game.kalah.domain.exception.InvalidPitException;
import nl.backbase.game.kalah.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest implements WithBDDMockito {

    @Mock
    private GameRepository gameRepository;

    private GameServiceImpl underTest;
    @Captor
    private ArgumentCaptor<Game> gameArgumentCaptor;

    @BeforeEach
    void setUp() {
        underTest = new GameServiceImpl(gameRepository);
    }

    @Test
    @DisplayName("Create a new game and save in database")
    void createGameHappy() {
        //when
        Game game = underTest.createGame();

        //then
        verify(gameRepository).save(gameArgumentCaptor.capture());
        assertNotNull(gameArgumentCaptor.getValue().getId());
        assertEquals(game.getGameStatus(), gameArgumentCaptor.getValue().getGameStatus());
        assertEquals(game.getId(), gameArgumentCaptor.getValue().getId());
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("coins should move to next pits / own kalah")
    void makeMoveHappy() {
        Game game = Game.builder()
                .gameStatus(GameStatus.CREATED)
                .id("12341234")
                .pits(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameRepository.findById("12341234")).willReturn(Optional.of((game)));
        //when
        Game result = underTest.makeMove("12341234", 1);

        //then
        assertEquals(Arrays.toString(new int[]{0, 5, 5, 5, 5, 4, 0, 4, 4, 4, 4, 4, 4, 0}), Arrays.toString(game.getPits()));
        verify(gameRepository).findById("12341234");
        verify(gameRepository).save(gameArgumentCaptor.capture());
        assertEquals(result, gameArgumentCaptor.getValue());
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("coins should not move to opponent kalah")
    void notMoveCoinsToOtherKalah() {
        Game game = Game.builder()
                .gameStatus(GameStatus.CREATED)
                .id("12341234")
                .pits(new int[]{1, 0, 0, 0, 0, 8, 15, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameRepository.findById("12341234")).willReturn(Optional.of((game)));
        //when
        Game result = underTest.makeMove("12341234", 6);

        //then
        assertEquals(Arrays.toString(new int[]{2, 0, 0, 0, 0, 0, 16, 5, 5, 5, 5, 5, 5, 0}), Arrays.toString(game.getPits()));
        verify(gameRepository).findById("12341234");
        verify(gameRepository).save(gameArgumentCaptor.capture());
        assertEquals(result, gameArgumentCaptor.getValue());
        assertEquals(GameStatus.IN_PROGRESS, result.getGameStatus());
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Move coins from opposite pit and own pit to Kalah When the last stone lands in an own empty pit ")
    void moveCointsToKalah_When_last_stone_in_empty_pit() {
        Game game = Game.builder()
                .gameStatus(GameStatus.IN_PROGRESS)
                .id("12341234")
                .pits(new int[]{1, 0, 0, 0, 0, 9, 14, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameRepository.findById("12341234")).willReturn(Optional.of((game)));
        //when
        Game result = underTest.makeMove("12341234", 6);

        //then
        assertEquals(Arrays.toString(new int[]{2, 0, 0, 0, 0, 0, 21, 5, 0, 5, 5, 5, 5, 0}), Arrays.toString(game.getPits()));
        verify(gameRepository).findById("12341234");
        verify(gameRepository).save(gameArgumentCaptor.capture());
        assertEquals(result, gameArgumentCaptor.getValue());
        assertEquals(GameStatus.IN_PROGRESS, result.getGameStatus());
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Game should be completed when one player pits becomes empty")
    void moveAllOtherCointsToKalah_When_one_player_pits_empty() {
        Game game = Game.builder()
                .gameStatus(GameStatus.IN_PROGRESS)
                .id("12341234")
                .pits(new int[]{0, 0, 0, 0, 0, 1, 22, 5, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameRepository.findById("12341234")).willReturn(Optional.of((game)));
        //when
        Game result = underTest.makeMove("12341234", 6);

        //then
        assertEquals(Arrays.toString(new int[]{0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 25}), Arrays.toString(game.getPits()));
        verify(gameRepository).findById("12341234");
        verify(gameRepository).save(gameArgumentCaptor.capture());
        assertEquals(result, gameArgumentCaptor.getValue());
        assertEquals(GameStatus.SECONDPLAYERWON, result.getGameStatus());
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should throw Invalid Pit exception when there are no coins in pit")
    void makeMove_when_pit_empty() {
        Game game = Game.builder()
                .gameStatus(GameStatus.IN_PROGRESS)
                .id("12341234")
                .pits(new int[]{4, 4, 0, 5, 5, 5, 1, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameRepository.findById("12341234")).willReturn(Optional.of((game)));
        //when
        assertThrows(InvalidPitException.class, () -> underTest.makeMove("12341234", 3));

        //then
        verify(gameRepository).findById("12341234");
        verifyNoMoreInteractions(gameRepository);
    }


    @Test
    @DisplayName("Should throw GameNotFoundException when game is is not found")
    void makeMove_when_game_not_found() {
        given(gameRepository.findById("12341234")).willThrow(GameNotFoundException.class);
        //when
        assertThrows(GameNotFoundException.class, () -> underTest.makeMove("12341234", 3));

        //then
        verify(gameRepository).findById("12341234");
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should throw GameCompletedException when game is completed")
    void makeMove_when_game_completed() {
        given(gameRepository.findById("12341234")).willThrow(GameCompletedException.class);
        //when
        assertThrows(GameCompletedException.class, () -> underTest.makeMove("12341234", 3));

        //then
        verify(gameRepository).findById("12341234");
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should return game based on gameId")
    void getGameHappy() {
        //given
        Game game = Game.builder()
                .gameStatus(GameStatus.CREATED)
                .id("12341234")
                .pits(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameRepository.findById("12341234")).willReturn(Optional.of(game));

        //when
        Game result = underTest.getGame("12341234");

        //then
        verify(gameRepository).findById("12341234");
        assertEquals(game, result);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should throw GameNotFoundException")
    void getGameUnHappy_when_id_not_found() {
        //given
        given(gameRepository.findById("12341234")).willThrow(GameNotFoundException.class);

        //when
        assertThrows(GameNotFoundException.class, () -> underTest.getGame("12341234"));

        //then
        verify(gameRepository).findById("12341234");
        verifyNoMoreInteractions(gameRepository);
    }

}

