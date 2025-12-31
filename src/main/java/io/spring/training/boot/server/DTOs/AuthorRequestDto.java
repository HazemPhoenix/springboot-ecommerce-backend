package io.spring.training.boot.server.DTOs;

public record AuthorRequestDto(String name, String bio, String nationality, String photo) {
}
