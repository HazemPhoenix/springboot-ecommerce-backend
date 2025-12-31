package io.spring.training.boot.server.DTOs;

public record AuthorDto(Long id, String name, String bio, String nationality, String photo) {
}
