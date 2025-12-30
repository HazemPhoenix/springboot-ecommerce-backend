package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.repositories.BookRepo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepo bookRepo;

    public BookDto createBook(Book book) {
        Book createdBook = bookRepo.save(book);
        return new BookDto(createdBook.getId(), createdBook.getTitle(), createdBook.getDescription(), createdBook.getPrice(), createdBook.getNumberOfPages(), createdBook.getImage());
    }

    public BookDto findBookById(long id){
        return bookRepo.findById(id)
                .map(book -> new BookDto(book.getId(), book.getTitle(), book.getDescription(), book.getPrice(), book.getNumberOfPages(), book.getImage()))
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public @Nullable List<BookDto> getAllBooks() {
        List<Book> books = bookRepo.findAll();
        return books.stream().map(book -> new BookDto(book.getId(), book.getTitle(), book.getDescription(), book.getPrice(), book.getNumberOfPages(), book.getImage())).toList();
    }
}
