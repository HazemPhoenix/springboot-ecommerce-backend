package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.GenreResponseDto;
import io.spring.training.boot.server.DTOs.GenreRequestDto;
import io.spring.training.boot.server.models.Genre;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public interface GenreService {
    GenreResponseDto findGenreById(Long id);

    List<GenreResponseDto> getAllGenres();

    GenreResponseDto createGenre(GenreRequestDto genreRequestDto);

    GenreResponseDto updateGenre(Long id, GenreRequestDto genreRequestDto);

    void deleteGenreById(Long id);

    Set<Genre> findGenresByIds(Set<Long> longs);
}
