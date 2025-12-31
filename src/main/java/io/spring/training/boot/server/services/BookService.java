package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.utils.mappers.BookMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepo bookRepo;

    public BookDto createBook(BookRequestDto bookRequest) {
        Book book = BookMapper.fromBookRequestDto(bookRequest);
        return BookMapper.toBookDto(bookRepo.save(book));
    }

    public BookDto findBookById(long id){
        return bookRepo.findById(id)
                .map(BookMapper::toBookDto)
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: " + id));
    }

    public @Nullable List<BookDto> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepo.findAll(pageable);
        return books.stream().map(BookMapper::toBookDto).toList();
    }

    public BookDto updateBookById(Long id, @Valid BookRequestDto bookRequest) {
        Optional<Book> oldBook = bookRepo.findById(id);

        if(oldBook.isEmpty()){
            throw new BookNotFoundException("No book found with the id: " + id);
        }

        Book newBook = BookMapper.fromBookRequestDto(bookRequest);
        newBook.setId(id);

        return BookMapper.toBookDto(bookRepo.save(newBook));
    }

    public void deleteBookById(Long id) {
        bookRepo.deleteById(id);
    }
}
