package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.services.GenreService;
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
    public ResponseEntity<List<GenreDto>> getAllGenres(){
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Long genreId) {
        return ResponseEntity.ok(genreService.findGenreById(genreId));
    }

    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@RequestBody GenreRequestDto genreRequestDto){
        GenreDto newGenre = genreService.createGenre(genreRequestDto);
        URI newGenreLocation = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{genreId}")
                .build(newGenre.id());
        return ResponseEntity.created(newGenreLocation).body(newGenre);
    }

    @PutMapping("/{genreId}")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable Long id, GenreRequestDto genreRequestDto){
        GenreDto newGenre = genreService.updateGenre(id, genreRequestDto);
        return ResponseEntity.ok(newGenre);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long genreId){
        genreService.deleteGenreById(genreId);
        return ResponseEntity.noContent().build();
    }

}
