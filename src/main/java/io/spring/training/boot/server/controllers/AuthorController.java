package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.services.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<Page<AuthorResponseDto>> getAllAuthors(@PageableDefault(size = 20) Pageable pageable, @RequestParam(required = false) String keyword){
        return ResponseEntity.ok().body(authorService.getAllAuthors(pageable, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable Long id){
        return ResponseEntity.ok().body(authorService.getAuthorById(id));
    }

}
