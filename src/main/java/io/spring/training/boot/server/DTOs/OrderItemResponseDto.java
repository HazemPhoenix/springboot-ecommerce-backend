package io.spring.training.boot.server.DTOs;

import java.math.BigDecimal;

public record OrderItemResponseDto(Long id,
                                   BookOrderSummaryDto book,
                                   Integer quantity,
                                   BigDecimal totalPrice) {
}
