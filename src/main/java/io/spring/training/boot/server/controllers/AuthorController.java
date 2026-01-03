package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.AuthorResponseDto;
import io.spring.training.boot.server.DTOs.AuthorRequestDto;
import io.spring.training.boot.server.services.AuthorService;
import io.spring.training.boot.server.services.AuthorServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorServiceImpl authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<Page<AuthorResponseDto>> getAllAuthors(@PageableDefault(size = 20) Pageable pageable, @RequestParam String keyword){
        return ResponseEntity.ok().body(authorService.getAllAuthors(pageable, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable Long id){
        return ResponseEntity.ok().body(authorService.getAuthorById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorResponseDto> createAuthor(@Valid @RequestPart AuthorRequestDto authorData, @RequestPart MultipartFile authorImage) {
        AuthorResponseDto authorResponseDto = authorService.createAuthor(authorData, authorImage);
        URI createdAuthorUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{authorId}")
                .build(authorResponseDto.id());
        return ResponseEntity.created(createdAuthorUri).body(authorResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> updateAuthor(@PathVariable Long id, @Valid @RequestPart AuthorRequestDto authorData, @RequestPart MultipartFile authorImage) {
        AuthorResponseDto authorResponseDto = authorService.updateAuthor(id, authorData, authorImage);
        return ResponseEntity.ok().body(authorResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id){
        authorService.deleteAuthorById(id);
        return ResponseEntity.noContent().build();
    }
}
