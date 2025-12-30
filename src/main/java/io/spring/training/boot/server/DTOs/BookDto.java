package io.spring.training.boot.server.DTOs;

import java.math.BigDecimal;

public record BookDto(Long id, String title, String description, BigDecimal price, int numberOfPages, String image) {
}
