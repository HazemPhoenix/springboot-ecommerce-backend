package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    BookResponseDto createBook(BookRequestDto bookRequest, MultipartFile bookImage);

    BookResponseDto findBookById(long id);

    Page<BookSummaryDto> getAllBooks(Pageable pageable, String keyword);

    BookResponseDto updateBookById(Long id, @Valid BookRequestDto bookRequest, MultipartFile bookImage);

    void deleteBookById(Long id);

    ReviewResponseDto createReviewForBook(Long bookId, ReviewRequestDto reviewRequestDto);

    ReviewResponseDto updateReviewForBook(Long bookId, ReviewRequestDto reviewRequestDto);

    Page<ReviewResponseDto> getReviewsForBook(Long bookId, Pageable pageable, String keyword);

    void deleteReview(Long bookId, Long reviewId);
}
