package nl.backbase.game.kalah.controller;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import nl.backbase.game.kalah.domain.Game;
import nl.backbase.game.kalah.domain.enums.GameStatus;
import nl.backbase.game.kalah.service.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = GameController.class)
public class GameControllerMvcTest implements WithBDDMockito {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameService gameService;

    @Test
    @DisplayName("Should respond with 201 and return GameResponse with ID & uri")
    void createGameHappy() throws Exception {
        //given
        Game game = Game.builder().id("12344").build();
        given(gameService.createGame()).willReturn(game);

        //when
        MockHttpServletResponse response = mockMvc
                .perform(post("/games")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertEquals(201, response.getStatus());
        verify(gameService).createGame();
        verifyNoMoreInteractions(gameService);
    }

    @Test
    @DisplayName("Should respond with 415 when content type is not present in headers")
    void createGameUnHappy_when_no_contentType() throws Exception {
        //given
        Game game = Game.builder().id("12344").build();
        given(gameService.createGame()).willReturn(game);

        //when
        MockHttpServletResponse response = mockMvc
                .perform(post("/games")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertEquals(415, response.getStatus());
        verifyNoMoreInteractions(gameService);
    }

    @Test
    @DisplayName("Should respond with 201 and return GameResponse with ID & uri")
    void getGameHappy() throws Exception {
        //given
        Game game = Game.builder().id("12345678")
                .pits(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameService.getGame("12345678")).willReturn(game);

        //when
        MockHttpServletResponse response = mockMvc
                .perform(get("/games/12345678")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertEquals(200, response.getStatus());
        verify(gameService).getGame("12345678");
        verifyNoMoreInteractions(gameService);
    }

    @Test
    @DisplayName("Should respond with 201 and return GameResponse with ID & uri")
    void getGameUnHappy_when_invalid_gameid() throws Exception {
        //given
        Game game = Game.builder().id("1234")
                .pits(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameService.getGame("1234")).willReturn(game);

        //when
        MockHttpServletResponse response = mockMvc
                .perform(get("/games/1234")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertEquals(400, response.getStatus());
        verifyNoMoreInteractions(gameService);
    }

    @Test
    @DisplayName("Should respond with 415 when content type is not present in headers")
    void getGameUnHappy_when_no_contentType() throws Exception {
        //given
        Game game = Game.builder().id("12344").build();
        given(gameService.getGame("1234")).willReturn(game);

        //when
        MockHttpServletResponse response = mockMvc
                .perform(get("/games")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertEquals(405, response.getStatus());
        verifyNoMoreInteractions(gameService);
    }

    @Test
    @DisplayName("Should respond with 200 and return GameResponse with ID & uri")
    void makeMoveHappy() throws Exception {
        //given
        Game game = Game.builder().id("12345678")
                .pits(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameService.makeMove("12345678", 1)).willReturn(game);

        //when
        MockHttpServletResponse response = mockMvc
                .perform(put("/games/12345678/pits/1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertEquals(200, response.getStatus());
        verify(gameService).makeMove("12345678", 1);
        verifyNoMoreInteractions(gameService);
    }

    @Test
    @DisplayName("Should respond with 200 and return GameResponse with ID & uri")
    void makeMoveHappy_when_secondplayer_move() throws Exception {
        //given
        Game game = Game.builder().id("12345678")
                .pits(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0})
                .gameStatus(GameStatus.IN_PROGRESS)
                .isSecondPlayerMove(true)
                .build();
        given(gameService.makeMove("12345678", 8)).willReturn(game);

        //when
        MockHttpServletResponse response = mockMvc
                .perform(put("/games/12345678/pits/8")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertEquals(200, response.getStatus());
        assertEquals(GameStatus.IN_PROGRESS, game.getGameStatus());
        verify(gameService).makeMove("12345678", 8);
        verifyNoMoreInteractions(gameService);
    }

    @Test
    @DisplayName("Should respond with 400 when pit id is more than 14")
    void makeMoveUnHappy_InvalidPit() throws Exception {
        //given
        Game game = Game.builder().id("12345678")
                .pits(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameService.makeMove("12345678", 1)).willReturn(game);

        //when
        MockHttpServletResponse response = mockMvc
                .perform(put("/games/12345678/pits/46")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid Pit"));
        verifyNoMoreInteractions(gameService);
    }

    @Test
    @DisplayName("Should respond with 400 when game id is not 8 characters")
    void makeMoveUnHappy_InvalidGameId() throws Exception {
        //given
        Game game = Game.builder().id("1234567811111")
                .pits(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameService.makeMove("1234567811111", 1)).willReturn(game);

        //when
        MockHttpServletResponse response = mockMvc
                .perform(put("/games/1234567811111/pits/4")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid Game ID"));
        verifyNoMoreInteractions(gameService);
    }

    @Test
    @DisplayName("Should respond with 400 when pit id is house")
    void makeMoveUnHappy_when_pit_id_is_kalah() throws Exception {
        //given
        Game game = Game.builder().id("12341234")
                .pits(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0})
                .build();
        given(gameService.makeMove("12341234", 1)).willReturn(game);

        //when
        MockHttpServletResponse response = mockMvc
                .perform(put("/games/12341234/pits/7")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("Coins from house should not be allowed to move"));
        verifyNoMoreInteractions(gameService);
    }
}
