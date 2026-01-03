package io.spring.training.boot.server.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AuthorRequestDto(@NotBlank(message = "Author name is required") String name,
                               String bio,
                               String nationality,
                               @NotNull(message = "Genre IDs are required") Set<Long> genreIDs
) {
}
