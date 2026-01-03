package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.BookResponseDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    BookResponseDto createBook(BookRequestDto bookRequest, MultipartFile bookImage);

    BookResponseDto findBookById(long id);

    Page<BookResponseDto> getAllBooks(Pageable pageable);

    BookResponseDto updateBookById(Long id, @Valid BookRequestDto bookRequest, MultipartFile bookImage);

    void deleteBookById(Long id);
}
