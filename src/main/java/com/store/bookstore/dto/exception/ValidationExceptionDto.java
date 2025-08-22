package com.store.bookstore.dto.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.http.HttpStatus;

public record ValidationExceptionDto(
        @JsonProperty("Status") HttpStatus responseStatus,
        @JsonProperty("Time") LocalDateTime localDateTime,
        @JsonProperty("Errors") Set<String> errorMessages
) {
    public ValidationExceptionDto(HttpStatus responseStatus, Set<String> errorMessages) {
        this(responseStatus, LocalDateTime.now(), errorMessages);
    }
}
