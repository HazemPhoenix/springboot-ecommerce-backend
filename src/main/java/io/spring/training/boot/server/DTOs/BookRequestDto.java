package io.spring.training.boot.server.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BookRequestDto(@NotBlank(message = "Title is required") String title,
                             @NotBlank(message = "Description is required") String description,
                             @Positive(message = "Price cannot be a negative value") BigDecimal price,
                             @Positive(message = "Number of pages cannot be negative") int numberOfPages,
                             String image) {
}
