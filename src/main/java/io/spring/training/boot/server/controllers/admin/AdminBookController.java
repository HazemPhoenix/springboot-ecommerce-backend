package io.spring.training.boot.server.controllers.admin;

import io.spring.training.boot.server.DTOs.book.BookCreationResponseDto;
import io.spring.training.boot.server.DTOs.book.BookRequestDto;
import io.spring.training.boot.server.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin/books")
@RequiredArgsConstructor
public class AdminBookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookCreationResponseDto> createBook(@Valid @RequestPart BookRequestDto bookData, @RequestPart MultipartFile bookImage){
        BookCreationResponseDto bookCreationResponseDto = bookService.createBook(bookData, bookImage);
        URI newBookUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{bookId}")
                .build(bookCreationResponseDto.id());
        return ResponseEntity.created(newBookUri).body(bookCreationResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookCreationResponseDto> updateBook(@Valid @RequestPart BookRequestDto bookData, @RequestPart MultipartFile bookImage, @PathVariable Long id){
        BookCreationResponseDto bookCreationResponseDto = bookService.updateBookById(id, bookData, bookImage);
        return ResponseEntity.ok().body(bookCreationResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}
