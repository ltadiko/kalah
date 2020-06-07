package nl.backbase.game.kalah.util;

import nl.backbase.game.kalah.utils.IdGeneratorUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdGeneratorUtilTest {

    @Test
    @DisplayName("Should generateRequestId 32 characters long alphanumeric requestId")
    void generateRequestId() {
        String generated = IdGeneratorUtil.generateRequestId();
        assertEquals(32, generated.length());
        assertTrue(generated.matches("[a-z0-9]*"));
    }

    @Test
    @DisplayName("Should generate unique id 8 character alphanumeric requestId")
    void generateGameId() {
        assertNotNull(IdGeneratorUtil.generateGameId());
    }

}