package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.book.BookResponseWithStats;
import io.spring.training.boot.server.DTOs.book.BookSummaryDto;
import io.spring.training.boot.server.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<BookSummaryDto>> getAllBooks(@PageableDefault(size = 20) Pageable pageable, @RequestParam(required = false) String keyword){
        return new ResponseEntity<>(bookService.getAllBooks(pageable, keyword), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseWithStats> getBookById(@PathVariable Long id){
        BookResponseWithStats book = bookService.findBookById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }
}
