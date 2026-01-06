package io.spring.training.boot.server.DTOs.order;

import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.models.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderUserResponseDto(Long id,
                                   OrderStatus status,
                                   PaymentMethod paymentMethod,
                                   BigDecimal totalAmount,
                                   LocalDate date,
                                   List<OrderItemResponseDto> orderItems) {
}
