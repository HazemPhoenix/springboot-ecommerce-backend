package io.spring.training.boot.server.DTOs.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDto(@NotNull(message = "Book id is required") @Positive(message = "Book id cannot be negative")
                                  Long bookId,
                                  @NotNull(message = "Quantity is required") @Positive(message = "Quantity must be a positive number (i.e. greater than zero)")
                                  Integer quantity) {
}
