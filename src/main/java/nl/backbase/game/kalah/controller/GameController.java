package nl.backbase.game.kalah.controller;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.game.kalah.domain.Game;
import nl.backbase.game.kalah.domain.enums.GameStatus;
import nl.backbase.game.kalah.domain.exception.InvalidGameException;
import nl.backbase.game.kalah.domain.exception.InvalidPitException;
import nl.backbase.game.kalah.domain.rest.GameResponse;
import nl.backbase.game.kalah.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /*
    Standard Format
    @PostMapping(path = "games", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<Game> createGame() {
        Game game = gameService.createGame();
        Link link = linkTo(methodOn(GameController.class).createGame())
                .slash(game.getId())
                .withRel("uri");
        game.add(link);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    */

    @PostMapping(path = "games", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse createGame() {
        Game game = gameService.createGame();
        Link link = linkTo(methodOn(GameController.class).createGame())
                .slash(game.getId())
                .withRel("uri");
        return GameResponse.builder().id(game.getId()).uri(link.getHref()).build();
    }

    @GetMapping(path = "games/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GameResponse getGame(@PathVariable String gameId) {
        if (gameId.length() != 8) {
            throw new InvalidGameException("Invalid Game ID");
        }
        Game game = gameService.getGame(gameId);
        return GameResponse.builder()
                .id(game.getId())
                .status(transformPlayerPitToStatus(game))
                .playerMessage(transformPlayerMessage(game))
                .build();
    }

    @PutMapping(path = "games/{gameId}/pits/{pitId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GameResponse makeMove(@PathVariable String gameId, @PathVariable int pitId) {
        if (gameId.length() != 8) {
            throw new InvalidGameException("Invalid Game ID");
        }
        if (pitId > 14 || pitId < 0) {
            throw new InvalidPitException("Invalid Pit");
        }
        if (pitId == 7 || pitId == 14) {
            throw new InvalidPitException("Coins from house should not be allowed to move.Please chose pit");
        }

        //validate pitId
        Game game = gameService.makeMove(gameId, pitId);

        return GameResponse.builder()
                .id(gameId)
                .status(transformPlayerPitToStatus(game))
                .id(game.getId())
                .playerMessage(transformPlayerMessage(game))
                .build();
    }

    private String transformPlayerMessage(Game game) {
        if (GameStatus.CREATED != game.getGameStatus() && GameStatus.IN_PROGRESS != game.getGameStatus()) {
            return "Game is completed with game status " + game.getGameStatus();
        }
        String playerMessage = "First Player should move the coin(s)";
        if (game.isSecondPlayerMove()) {
            playerMessage = "Second Player should move the coin(s)";
        }
        return playerMessage;
    }

    private Map<Integer, Integer> transformPlayerPitToStatus(Game game) {
        Map<Integer, Integer> status = new HashMap<>();
        for (int i = 1; i < 15; i++) {
            status.put(i, game.getPits()[i - 1]);
        }
        return status;
    }
}
