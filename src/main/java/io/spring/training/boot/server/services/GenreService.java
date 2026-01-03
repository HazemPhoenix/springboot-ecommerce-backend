package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.GenreResponseDto;
import io.spring.training.boot.server.DTOs.GenreRequestDto;

import java.util.List;

public interface GenreService {
    GenreResponseDto findGenreById(Long id);

    List<GenreResponseDto> getAllGenres();

    GenreResponseDto createGenre(GenreRequestDto genreRequestDto);

    GenreResponseDto updateGenre(Long id, GenreRequestDto genreRequestDto);

    void deleteGenreById(Long id);
}
