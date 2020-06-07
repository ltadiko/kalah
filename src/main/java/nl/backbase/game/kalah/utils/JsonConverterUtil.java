package nl.backbase.game.kalah.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.backbase.game.kalah.domain.exception.JsonConversionException;

import java.io.*;

/**
 * JsonConverterUtil helps to convert object to JSON string
 */
public final class JsonConverterUtil {

    private JsonConverterUtil() {
    }

    /**
     * converts any object to json string
     * <p>
     * {@link ObjectMapper#writeValueAsString(Object)}
     *
     * @param object objects which needs json string
     * @return json string
     */
    public static String convertToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonConversionException("Exception while converting to JSON", e);
        }

    }

}
