package nl.backbase.game.kalah.util;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import nl.backbase.game.kalah.domain.Game;
import nl.backbase.game.kalah.domain.enums.GameStatus;
import nl.backbase.game.kalah.domain.exception.JsonConversionException;
import nl.backbase.game.kalah.utils.JsonConverterUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonConverterUtilTest implements WithBDDMockito {

    @Test
    @DisplayName("Should convert Name to Json")
    void convertToJson() {
        // given
        Game game = Game.builder().id("12341234").gameStatus(GameStatus.DRAW).build();
        // when
        String json = JsonConverterUtil.convertToJson(game);

        // then
        assertEquals("{\"id\":\"12341234\",\"pits\":null,\"gameStatus\":\"DRAW\",\"secondPlayerMove\":false}", json);
    }

    @Test
    @DisplayName("Should throw JsonConversionException while converting mock object")
    void exceptionConvToJson() {
        Game notSerilizableObj = mock(Game.class);

        assertThrows(JsonConversionException.class, () -> JsonConverterUtil.convertToJson(notSerilizableObj));
    }

}
