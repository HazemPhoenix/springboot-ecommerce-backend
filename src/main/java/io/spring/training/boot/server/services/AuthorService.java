package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.AuthorDto;
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
}
