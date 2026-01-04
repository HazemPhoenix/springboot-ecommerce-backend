package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.ReviewRequestDto;
import io.spring.training.boot.server.DTOs.ReviewResponseDto;
import io.spring.training.boot.server.models.Review;

public class ReviewMapper {
    public static ReviewResponseDto toReviewResponseDto(Review review) {
        return new ReviewResponseDto(review.getId().getUserId(),review.getId().getBookId(), review.getRating(), review.getTitle(), review.getContent(), review.isEdited());
    }

    public static Review fromReviewRequestDto(ReviewRequestDto requestDto) {
        return new Review(requestDto.rating(), requestDto.title(), requestDto.content());
    }
}
