package io.spring.training.boot.server.exceptions;

public class ImageNotFoundException extends StorageException {
    public ImageNotFoundException(String message) {
        super(message);
    }
}
