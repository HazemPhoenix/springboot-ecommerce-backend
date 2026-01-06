package io.spring.training.boot.server.DTOs.order;

import io.spring.training.boot.server.DTOs.book.BookOrderSummaryDto;

import java.math.BigDecimal;

public record OrderItemResponseDto(Long id,
                                   BookOrderSummaryDto book,
                                   Integer quantity,
                                   BigDecimal totalPrice) {
}
