package io.spring.training.boot.server.security.services;

public interface JwtService {
    String generateToken(String email);
    String extractEmail(String token);
    boolean isTokenValid(String token);
}
