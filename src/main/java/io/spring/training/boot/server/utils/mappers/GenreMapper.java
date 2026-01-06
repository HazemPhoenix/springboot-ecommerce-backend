package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.genre.GenreRequestDto;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.models.Genre;

public class GenreMapper {
    public static GenreResponseDto toGenreResponseDto(Genre genre) {
        return new GenreResponseDto(genre.getId(), genre.getName());
    }

    public static Genre fromGenreRequestDto(GenreRequestDto genreRequestDto){
        return new Genre(genreRequestDto.name());
    }
}
