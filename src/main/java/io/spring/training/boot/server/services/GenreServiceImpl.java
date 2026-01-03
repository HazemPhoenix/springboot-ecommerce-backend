package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.GenreRequestDto;
import io.spring.training.boot.server.DTOs.GenreResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {
    @Override
    public GenreResponseDto findGenreById(Long id) {
        return null;
    }

    @Override
    public List<GenreResponseDto> getAllGenres() {
        return List.of();
    }

    @Override
    public GenreResponseDto createGenre(GenreRequestDto genreRequestDto) {
        return null;
    }

    @Override
    public GenreResponseDto updateGenre(Long id, GenreRequestDto genreRequestDto) {
        return null;
    }

    @Override
    public void deleteGenreById(Long id) {

    }
}
