package io.spring.training.boot.server.exceptions;

import org.springframework.web.bind.annotation.RestControllerAdvice;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
