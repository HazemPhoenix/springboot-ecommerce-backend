package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.services.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
