package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.AuthorDto;
import io.spring.training.boot.server.DTOs.AuthorRequestDto;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.services.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<Page<AuthorDto>> getAllAuthors(@PageableDefault(size = 20) Pageable pageable){
        return ResponseEntity.ok().body(authorService.getAllAuthors(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable Long id){
        return ResponseEntity.ok().body(authorService.getAuthorById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@Valid @RequestPart AuthorRequestDto authorData, @RequestPart MultipartFile authorImage) {
        AuthorDto authorDto = authorService.createAuthor(authorData, authorImage);
        URI createdAuthorUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{authorId}")
                .build(authorDto.id());
        return ResponseEntity.created(createdAuthorUri).body(authorDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable Long id, @Valid @RequestPart AuthorRequestDto authorData, @RequestPart MultipartFile authorImage) {
        AuthorDto authorDto = authorService.updateAuthor(id, authorData, authorImage);
        return ResponseEntity.ok().body(authorDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id){
        authorService.deleteAuthorById(id);
        return ResponseEntity.noContent().build();
    }
}
