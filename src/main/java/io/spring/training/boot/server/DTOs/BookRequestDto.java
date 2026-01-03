package io.spring.training.boot.server.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Set;

public record BookRequestDto(@NotBlank(message = "Title is required") String title,
                             @NotBlank(message = "Description is required") String description,
                             @NotNull(message = "Price is required") @Positive(message = "Price cannot be a negative value") BigDecimal price,
                             @NotNull(message = "Number of pages is required") @Positive(message = "Number of pages cannot be negative") Integer numberOfPages,
                             @NotNull(message = "Author IDs are required") Set<Long> authorIDs,
                             @NotNull(message = "Genre IDs are required") Set<Long> genreIDs
                             ) {
}
