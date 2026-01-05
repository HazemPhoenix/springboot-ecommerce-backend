package io.spring.training.boot.server.exceptions;

public class ReviewNotFoundException extends ResourceNotFoundException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
