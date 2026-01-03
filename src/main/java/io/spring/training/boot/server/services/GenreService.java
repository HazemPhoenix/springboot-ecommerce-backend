package io.spring.training.boot.server.services;

import io.spring.training.boot.server.models.Genre;

import java.util.List;

public interface GenreService {
    GenreDto findGenreById(Long id);

    List<GenreDto> getAllGenres();

    GenreDto createGenre(GenreRequestDto genreRequestDto);

    GenreDto updateGenre(Long id, GenreRequestDto genreRequestDto);

    void deleteGenreById(Long id);
}
