package com.store.bookstore.dto.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record ExceptionDto(
        HttpStatus responseStatus,
        LocalDateTime localDateTime,
        String errorMessages
) {
    public ExceptionDto(HttpStatus responseStatus, String errorMessages) {
        this(responseStatus, LocalDateTime.now(), errorMessages);
    }
}
