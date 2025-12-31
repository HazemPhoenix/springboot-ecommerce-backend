package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.AuthorDto;
import io.spring.training.boot.server.services.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<Page<AuthorDto>> getAllAuthors(@PageableDefault(size = 20) Pageable pageable){
        return ResponseEntity.ok().body(authorService.getAllAuthors(pageable));
    }
}
