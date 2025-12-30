package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.utils.mappers.BookMapper;
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
        return BookMapper.toDto(book);
    }

    public BookDto findBookById(long id){
        return bookRepo.findById(id)
                .map(BookMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public @Nullable List<BookDto> getAllBooks() {
        List<Book> books = bookRepo.findAll();
        return books.stream().map(BookMapper::toDto).toList();
    }
}
