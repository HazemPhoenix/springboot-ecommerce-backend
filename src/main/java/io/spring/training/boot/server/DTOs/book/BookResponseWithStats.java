package io.spring.training.boot.server.DTOs.book;

import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.DTOs.review.ReviewResponseDto;
import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;

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
