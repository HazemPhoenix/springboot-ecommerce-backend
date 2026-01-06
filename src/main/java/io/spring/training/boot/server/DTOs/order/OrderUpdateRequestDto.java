package io.spring.training.boot.server.DTOs.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderUpdateRequestDto(@Size(min = 1, message = "The order must contain at least 1 order item")
                                    @Valid List<OrderItemRequestDto> orderItems) {
}
