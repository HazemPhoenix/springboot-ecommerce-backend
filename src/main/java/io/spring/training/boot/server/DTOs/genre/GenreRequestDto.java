package io.spring.training.boot.server.DTOs.genre;

import jakarta.validation.constraints.NotBlank;

public record GenreRequestDto(@NotBlank(message = "Genre name is required") String name) {
}
