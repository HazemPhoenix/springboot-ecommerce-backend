package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.repositories.AuthorRepo;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.utils.mappers.AuthorMapper;
import io.spring.training.boot.server.utils.mappers.BookMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepo bookRepo;
    private final AuthorService authorService;

    public BookDto createBook(BookRequestDto bookRequest) {
        Book book = BookMapper.fromBookRequestDto(bookRequest);
        Set<Author> authors = authorService.findAuthorsByIds(bookRequest.authorIDs());
        book.setAuthors(authors);
        return BookMapper.toBookDto(bookRepo.save(book));
    }

    public BookDto findBookById(long id){
        return bookRepo.findById(id)
                .map(BookMapper::toBookDto)
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: " + id));
    }

    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepo.findAll(pageable).map(BookMapper::toBookDto);
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
