package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.AuthorDto;
import io.spring.training.boot.server.DTOs.AuthorRequestDto;
import io.spring.training.boot.server.exceptions.AuthorNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.repositories.AuthorRepo;
import io.spring.training.boot.server.utils.mappers.AuthorMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepo authorRepo;
    private final ImageStorageService imageStorageService;

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

    public AuthorDto updateAuthor(Long id, @Valid AuthorRequestDto authorRequestDto) {
        Optional<Author> oldAuthor = authorRepo.findById(id);

        if(oldAuthor.isEmpty()) {
            throw new AuthorNotFoundException("No author found with the id: " + id);
        }

        Author newAuthor = AuthorMapper.fromAuthorRequestDto(authorRequestDto);
        newAuthor.setId(id);

        return AuthorMapper.toAuthorDto(authorRepo.save(newAuthor));
    }

    public void deleteAuthorById(Long id) {
        authorRepo.deleteById(id);
    }

    public Set<Author> findAuthorsByIds(Set<Long> ids) {
        return new HashSet<>(authorRepo.findAllById(ids));
    }
}
