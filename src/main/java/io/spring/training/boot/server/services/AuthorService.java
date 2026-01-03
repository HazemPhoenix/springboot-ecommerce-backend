package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.AuthorDto;
import io.spring.training.boot.server.DTOs.AuthorRequestDto;
import io.spring.training.boot.server.models.Author;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface AuthorService {
    Page<AuthorDto> getAllAuthors(Pageable pageable);

    AuthorDto getAuthorById(Long id);

    AuthorDto createAuthor(AuthorRequestDto authorRequestDto, MultipartFile authorImage);

    AuthorDto updateAuthor(Long id, @Valid AuthorRequestDto authorRequestDto, MultipartFile authorImage);

    void deleteAuthorById(Long id);

    Set<Author> findAuthorsByIds(Set<Long> ids);
}
