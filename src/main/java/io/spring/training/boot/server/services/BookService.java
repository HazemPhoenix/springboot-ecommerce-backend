package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.utils.mappers.BookMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.mapper.Mapper;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepo bookRepo;

    public BookDto createBook(BookRequestDto bookRequest) {
        Book book = BookMapper.fromDto(bookRequest);
        return BookMapper.toDto(bookRepo.save(book));
    }

    public BookDto findBookById(long id){
        return bookRepo.findById(id)
                .map(BookMapper::toDto)
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: " + id));
    }

    public @Nullable List<BookDto> getAllBooks() {
        List<Book> books = bookRepo.findAll();
        return books.stream().map(BookMapper::toDto).toList();
    }

    public BookDto updateBookById(Long id, @Valid BookRequestDto bookRequest) {
        Optional<Book> oldBook = bookRepo.findById(id);

        if(oldBook.isEmpty()){
            throw new BookNotFoundException("No book found with the id: " + id);
        }

        Book newBook = BookMapper.fromDto(bookRequest);
        newBook.setId(id);

        return BookMapper.toDto(bookRepo.save(newBook));
    }
}
