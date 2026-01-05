package io.spring.training.boot.server.DTOs;

import java.math.BigDecimal;

public record BookOrderSummaryDto(Long id,
                                  String title,
                                  BigDecimal price) {
}
