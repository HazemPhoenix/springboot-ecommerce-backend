package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.AuthorResponseDto;
import io.spring.training.boot.server.DTOs.AuthorRequestDto;
import io.spring.training.boot.server.models.Author;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface AuthorService {
    Page<AuthorResponseDto> getAllAuthors(Pageable pageable);

    AuthorResponseDto getAuthorById(Long id);

    AuthorResponseDto createAuthor(AuthorRequestDto authorRequestDto, MultipartFile authorImage);

    AuthorResponseDto updateAuthor(Long id, @Valid AuthorRequestDto authorRequestDto, MultipartFile authorImage);

    void deleteAuthorById(Long id);

    Set<Author> findAuthorsByIds(Set<Long> ids);
}
