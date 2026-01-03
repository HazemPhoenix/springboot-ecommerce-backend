package io.spring.training.boot.server.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDto(@NotNull(message = "Book id is required") Long bookId,
                               @NotNull(message = "Rating is required") @Min(1) @Max(5) int rating,
                               String title,
                               String content) {
}
