package io.spring.training.boot.server.DTOs;

import java.math.BigDecimal;
import java.util.List;

public record BookResponseWithStats(Long id,
                                    String title,
                                    String description,
                                    BigDecimal price,
                                    int numberOfPages,
                                    int stock,
                                    String image,
                                    Integer totalReviews,
                                    Double averageRating,
                                    List<AuthorResponseDto> authors,
                                    List<GenreResponseDto> genres,
                                    List<ReviewResponseDto> reviews) {
}
