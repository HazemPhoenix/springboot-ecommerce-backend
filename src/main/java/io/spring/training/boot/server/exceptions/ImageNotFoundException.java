package io.spring.training.boot.server.exceptions;

public class ImageNotFoundException extends ResourceNotFoundException {
    public ImageNotFoundException(String message) {
        super(message);
    }
}
