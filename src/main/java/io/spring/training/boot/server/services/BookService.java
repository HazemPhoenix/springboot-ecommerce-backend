package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    BookDto createBook(BookRequestDto bookRequest, MultipartFile bookImage);

    BookDto findBookById(long id);

    Page<BookDto> getAllBooks(Pageable pageable);

    BookDto updateBookById(Long id, @Valid BookRequestDto bookRequest, MultipartFile bookImage);

    void deleteBookById(Long id);
}
