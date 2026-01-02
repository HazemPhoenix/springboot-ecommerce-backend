package io.spring.training.boot.server.DTOs;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequestDto(@NotBlank(message = "Author name is required") String name,
                               String bio,
                               String nationality) {
}
