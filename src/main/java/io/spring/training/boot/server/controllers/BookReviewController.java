package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.ReviewRequestDto;
import io.spring.training.boot.server.DTOs.ReviewResponseDto;
import io.spring.training.boot.server.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/books/{bookId}/reviews")
@RequiredArgsConstructor
public class BookReviewController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewRequestDto reviewRequestDto, @PathVariable Long bookId){
        ReviewResponseDto reviewResponseDto = bookService.createOrUpdateReviewForBook(bookId, reviewRequestDto);
        URI newReviewUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{userId}")
                .build(reviewResponseDto.userId());
        return ResponseEntity.created(newReviewUri).body(reviewResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponseDto>> getReviewsForBook(@PathVariable Long bookId,
                                                                     @PageableDefault(size = 20) Pageable pageable,
                                                                     @RequestParam(required = false) String keyword){
        Page<ReviewResponseDto> reviews = bookService.getReviewsForBook(bookId, pageable, keyword);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteReview(@PathVariable Long bookId, @PathVariable Long userId){
        bookService.deleteReview(bookId, userId);
        return ResponseEntity.noContent().build();
    }
}
