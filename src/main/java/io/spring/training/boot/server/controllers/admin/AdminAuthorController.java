package io.spring.training.boot.server.controllers.admin;

import io.spring.training.boot.server.DTOs.author.AuthorRequestDto;
import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.services.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin/authors")
@RequiredArgsConstructor
public class AdminAuthorController {
    private final AuthorService authorService;

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
