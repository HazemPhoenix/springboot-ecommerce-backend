package io.spring.training.boot.server.DTOs.book;

import java.math.BigDecimal;
import java.util.List;

public record BookSummaryDto(Long id, String title, BigDecimal price, String image, List<String> authors, int totalReviews, double averageRating) {
}
