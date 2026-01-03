package io.spring.training.boot.server.DTOs;

public record AuthorResponseDto(Long id, String name, String bio, String nationality, String photo) {
}
