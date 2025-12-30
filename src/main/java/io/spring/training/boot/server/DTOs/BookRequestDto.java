package io.spring.training.boot.server.DTOs;

import java.math.BigDecimal;

public record BookRequestDto(String title, String description, BigDecimal price, int numberOfPages, String image) {
}
