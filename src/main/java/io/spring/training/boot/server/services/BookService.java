package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.utils.mappers.BookMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Service
public class BookService {
    private final BookRepo bookRepo;
    private final AuthorService authorService;
    private final ImageStorageService imageStorageService;

    public BookService(BookRepo bookRepo, AuthorService authorService, FileSystemImageStorageService imageStorageService) {
        this.bookRepo = bookRepo;
        this.authorService = authorService;
        this.imageStorageService = imageStorageService;
    }

    public BookDto createBook(BookRequestDto bookRequest, MultipartFile bookImage) {
        Book book = BookMapper.fromBookRequestDto(bookRequest);

        Set<Author> authors = authorService.findAuthorsByIds(bookRequest.authorIDs());
        book.setAuthors(authors);

        if(!bookImage.isEmpty()){
            String imageName = imageStorageService.storeBookImage(bookImage);
            book.setImage(imageName);
        }

        return BookMapper.toBookDto(bookRepo.save(book));
    }

    public BookDto findBookById(long id){
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException("No book found with the id: " + id));
        String imageUrl = imageStorageService.getBookImage(book.getImage());
        book.setImage(imageUrl);
        return BookMapper.toBookDto(book);
    }

    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepo.findAll(pageable).map(BookMapper::toBookDto);
    }

    public BookDto updateBookById(Long id, @Valid BookRequestDto bookRequest, MultipartFile bookImage) {
        Optional<Book> oldBook = bookRepo.findById(id);

        if(oldBook.isEmpty()){
            throw new BookNotFoundException("No book found with the id: " + id);
        }

        Book newBook = BookMapper.fromBookRequestDto(bookRequest);
        newBook.setId(id);

        if(!bookImage.isEmpty()){
            String imageName = imageStorageService.storeBookImage(bookImage);
            newBook.setImage(imageName);
        }

        return BookMapper.toBookDto(bookRepo.save(newBook));
    }

    public void deleteBookById(Long id) {
        bookRepo.deleteById(id);
    }
}
