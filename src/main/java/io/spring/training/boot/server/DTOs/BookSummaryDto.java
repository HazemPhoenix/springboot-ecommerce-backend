package io.spring.training.boot.server.DTOs;

import java.math.BigDecimal;
import java.util.List;

public record BookSummaryDto(Long id, String title, BigDecimal price, String image, List<SimpleAuthorDto> authors) {
}
