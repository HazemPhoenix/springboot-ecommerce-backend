package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.book.BookCreationResponseDto;
import io.spring.training.boot.server.DTOs.book.BookRequestDto;
import io.spring.training.boot.server.DTOs.book.BookResponseWithStats;
import io.spring.training.boot.server.DTOs.book.BookSummaryDto;
import io.spring.training.boot.server.DTOs.review.ReviewRequestDto;
import io.spring.training.boot.server.DTOs.review.ReviewResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    BookCreationResponseDto createBook(BookRequestDto bookRequest, MultipartFile bookImage);

    BookResponseWithStats findBookById(long id);

    Page<BookSummaryDto> getAllBooks(Pageable pageable, String keyword);

    BookCreationResponseDto updateBookById(Long id, @Valid BookRequestDto bookRequest, MultipartFile bookImage);

    void deleteBookById(Long id);

    ReviewResponseDto createReviewForBook(Long bookId, ReviewRequestDto reviewRequestDto);

    ReviewResponseDto updateReviewForBook(Long bookId, ReviewRequestDto reviewRequestDto);

    Page<ReviewResponseDto> getReviewsForBook(Long bookId, Pageable pageable, String keyword);

    void deleteReviewForAdmin(Long bookId, Long reviewId);

    void deleteReviewForUser(Long bookId);
}
