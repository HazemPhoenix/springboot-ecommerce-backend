package io.spring.training.boot.server.exceptions;

public class AuthorNotFoundException extends ResourceNotFoundException {
    public AuthorNotFoundException(String message) {
        super(message);
    }
}
