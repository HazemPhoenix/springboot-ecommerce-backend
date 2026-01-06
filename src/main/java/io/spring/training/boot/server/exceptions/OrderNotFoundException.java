package io.spring.training.boot.server.exceptions;

public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(Long orderId) {
        super("order", orderId);
    }
}
