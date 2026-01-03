package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.BookResponseDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.DTOs.BookSummaryDto;
import io.spring.training.boot.server.services.BookService;
import io.spring.training.boot.server.services.BookServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookServiceImpl bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<BookSummaryDto>> getAllBooks(@PageableDefault(size = 20) Pageable pageable){
        return new ResponseEntity<>(bookService.getAllBooks(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id){
        BookResponseDto book = bookService.findBookById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BookResponseDto> createBook(@Valid @RequestPart BookRequestDto bookData, @RequestPart MultipartFile bookImage){
        BookResponseDto bookResponseDto = bookService.createBook(bookData, bookImage);
        URI newBookUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{bookId}")
                .build(bookResponseDto.id());
        return ResponseEntity.created(newBookUri).body(bookResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> updateBook(@Valid @RequestPart BookRequestDto bookData, @RequestPart MultipartFile bookImage, @PathVariable Long id){
        BookResponseDto bookResponseDto = bookService.updateBookById(id, bookData, bookImage);
        return ResponseEntity.ok().body(bookResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}
