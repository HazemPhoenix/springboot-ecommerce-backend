package io.spring.training.boot.server.controllers.admin;

import io.spring.training.boot.server.DTOs.genre.GenreRequestDto;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.services.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin/genres")
@RequiredArgsConstructor
public class AdminGenreController {
    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<GenreResponseDto> createGenre(@Valid @RequestBody GenreRequestDto genreRequestDto){
        GenreResponseDto newGenre = genreService.createGenre(genreRequestDto);
        URI newGenreLocation = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{genreId}")
                .build(newGenre.id());
        return ResponseEntity.created(newGenreLocation).body(newGenre);
    }

    @PutMapping("/{genreId}")
    public ResponseEntity<GenreResponseDto> updateGenre(@PathVariable Long genreId, @Valid @RequestBody GenreRequestDto genreRequestDto){
        GenreResponseDto newGenre = genreService.updateGenre(genreId, genreRequestDto);
        return ResponseEntity.ok(newGenre);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long genreId){
        genreService.deleteGenreById(genreId);
        return ResponseEntity.noContent().build();
    }
}
