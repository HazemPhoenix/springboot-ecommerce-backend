package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.AuthorDto;
import io.spring.training.boot.server.DTOs.AuthorRequestDto;
import io.spring.training.boot.server.exceptions.AuthorNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.repositories.AuthorRepo;
import io.spring.training.boot.server.utils.mappers.AuthorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepo authorRepo;

    public Page<AuthorDto> getAllAuthors(Pageable pageable){
        return authorRepo.findAll(pageable).map(AuthorMapper::toAuthorDto);
    }

    public AuthorDto getAuthorById(Long id) {
        return authorRepo.findById(id)
                .map(AuthorMapper::toAuthorDto)
                .orElseThrow(() -> new AuthorNotFoundException("No author found with the id: " + id));
    }

    public AuthorDto createAuthor(AuthorRequestDto authorRequestDto) {
        Author author = authorRepo.save(AuthorMapper.fromAuthorRequestDto(authorRequestDto));
        return AuthorMapper.toAuthorDto(author);
    }
}
