package io.spring.training.boot.server.services;

import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.repositories.BookRepo;
import lombok.AllArgsConstructor;
import lombok.Lombok;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BookService {
    private final BookRepo bookRepo;

    public Book createBook(Book book) {
        return bookRepo.save(book);
    }

    public Book findBookById(long id){
        Optional<Book> book = bookRepo.findById(id);
        return book.orElse(new Book());
    }
}
