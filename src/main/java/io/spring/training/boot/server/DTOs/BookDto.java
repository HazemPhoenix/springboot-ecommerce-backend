package io.spring.training.boot.server.DTOs;

import java.math.BigDecimal;
import java.util.List;

public record BookDto(Long id, String title, String description, BigDecimal price, int numberOfPages, String image, List<AuthorDto> authors) {
}
