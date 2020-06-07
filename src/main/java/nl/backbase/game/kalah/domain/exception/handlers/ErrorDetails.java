package nl.backbase.game.kalah.domain.exception.handlers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private Integer code;
}