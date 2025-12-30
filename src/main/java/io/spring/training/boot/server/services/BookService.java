package io.spring.training.boot.server.services;

import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.repositories.BookRepo;
import lombok.AllArgsConstructor;
import lombok.Lombok;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookService {
    private final BookRepo bookRepo;

    public Book createBook(Book book) {
        return bookRepo.save(book);
    }

    public Optional<Book> findBookById(long id){
        return bookRepo.findById(id);
    }

    public @Nullable List<Book> getAllBooks() {
        return bookRepo.findAll();
    }
}
