package io.spring.training.boot.server.DTOs.review;

public record ReviewResponseDto(Long userId,
                                Long bookId,
                                int rating,
                                String title,
                                String content,
                                boolean wasEdited) {
}
