package io.spring.training.boot.server.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super("No " + resourceName + " found with the id: " + resourceId);
    }
}
