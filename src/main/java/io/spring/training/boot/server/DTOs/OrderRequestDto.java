package io.spring.training.boot.server.DTOs;

import io.spring.training.boot.server.models.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderRequestDto(@NotNull(message = "Payment method is required")
                              PaymentMethod paymentMethod,
                              @Size(min = 1, message = "The order must contain at least 1 order item")
                              @Valid List<OrderItemRequestDto> orderItems){
}
