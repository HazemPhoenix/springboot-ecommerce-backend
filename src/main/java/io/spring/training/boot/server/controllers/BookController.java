package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.services.BookService;
import io.spring.training.boot.server.services.BookServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookServiceImpl bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<BookDto>> getAllBooks(@PageableDefault(size = 20) Pageable pageable){
        return new ResponseEntity<>(bookService.getAllBooks(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id){
        BookDto book = bookService.findBookById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestPart BookRequestDto bookData, @RequestPart MultipartFile bookImage){
        BookDto bookDto = bookService.createBook(bookData, bookImage);
        URI newBookUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{bookId}")
                .build(bookDto.id());
        return ResponseEntity.created(newBookUri).body(bookDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@Valid @RequestPart BookRequestDto bookData, @RequestPart MultipartFile bookImage, @PathVariable Long id){
        BookDto bookDto = bookService.updateBookById(id, bookData, bookImage);
        return ResponseEntity.ok().body(bookDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}
