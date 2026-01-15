package io.spring.training.boot.server.DTOs.auth;

public record LoginResponseDto(String email,
                               String token) {
}
