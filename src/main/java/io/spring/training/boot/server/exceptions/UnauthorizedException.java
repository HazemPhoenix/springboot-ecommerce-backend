package io.spring.training.boot.server.exceptions;


import org.springframework.security.access.AccessDeniedException;

public class UnauthorizedException extends AccessDeniedException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
