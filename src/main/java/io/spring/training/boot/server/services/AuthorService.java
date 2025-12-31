package io.spring.training.boot.server.services;

import io.spring.training.boot.server.repositories.AuthorRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepo authorRepo;
}
