package io.spring.training.boot.server.DTOs;

import java.math.BigDecimal;
import java.util.List;

public record BookResponseDto(Long id,
                              String title,
                              String description,
                              BigDecimal price,
                              int numberOfPages,
                              String image,
                              List<AuthorResponseDto> authors,
                              List<GenreResponseDto> genres) {
}
