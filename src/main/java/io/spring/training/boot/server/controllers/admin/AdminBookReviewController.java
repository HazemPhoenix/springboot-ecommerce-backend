package io.spring.training.boot.server.controllers.admin;

import io.spring.training.boot.server.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/books/{bookId}/reviews")
@RequiredArgsConstructor
public class AdminBookReviewController {
    private final BookService bookService;

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long bookId, @PathVariable Long userId){
        bookService.deleteReviewForAdmin(bookId, userId);
        return ResponseEntity.noContent().build();
    }
}
