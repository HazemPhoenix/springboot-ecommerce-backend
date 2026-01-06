package io.spring.training.boot.server.DTOs.order;

import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.models.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderSummaryDto(Long id,
                              OrderStatus status,
                              PaymentMethod paymentMethod,
                              BigDecimal totalAmount,
                              LocalDate date) {
}
