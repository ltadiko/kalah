package nl.backbase.game.kalah.repository;

import nl.backbase.game.kalah.domain.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {

}
