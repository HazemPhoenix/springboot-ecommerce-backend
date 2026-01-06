package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.DTOs.genre.GenreRequestDto;
import io.spring.training.boot.server.models.Genre;

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
