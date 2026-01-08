package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.DTOs.genre.GenreRequestDto;
import io.spring.training.boot.server.services.GenreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<GenreResponseDto>> getAllGenres(){
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<GenreResponseDto> getGenreById(@PathVariable Long genreId) {
        return ResponseEntity.ok(genreService.findGenreById(genreId));
    }

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
    public ResponseEntity<GenreResponseDto> updateGenre(@PathVariable Long id, GenreRequestDto genreRequestDto){
        GenreResponseDto newGenre = genreService.updateGenre(id, genreRequestDto);
        return ResponseEntity.ok(newGenre);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long genreId){
        genreService.deleteGenreById(genreId);
        return ResponseEntity.noContent().build();
    }

}
