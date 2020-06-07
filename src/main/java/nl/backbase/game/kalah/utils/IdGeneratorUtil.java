package nl.backbase.game.kalah.utils;

import java.util.*;

/**
 * Util class to generate unique ids
 */
public class IdGeneratorUtil {

    private IdGeneratorUtil() {
    }

    /**
     * Generates a new unique Id
     *
     * @return a 32 character long alphanumeric String
     */
    public static String generateRequestId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * Generates a new game Id
     *
     * @return a 8 character long alphanumeric String
     */
    public static String generateGameId() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    }

}
