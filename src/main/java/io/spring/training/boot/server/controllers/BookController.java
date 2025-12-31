package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks(Pageable pageable){
        return new ResponseEntity<>(bookService.getAllBooks(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id){
        BookDto book = bookService.findBookById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookRequestDto bookRequest){
        BookDto bookDto = bookService.createBook(bookRequest);
        URI newBookUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{bookId}")
                .build(bookDto.id());
        return ResponseEntity.created(newBookUri).body(bookDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@Valid @RequestBody BookRequestDto bookRequest, @PathVariable Long id){
        BookDto bookDto = bookService.updateBookById(id, bookRequest);
        return ResponseEntity.ok().body(bookDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}
