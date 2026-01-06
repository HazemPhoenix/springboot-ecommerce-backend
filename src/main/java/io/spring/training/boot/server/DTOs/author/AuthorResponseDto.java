package io.spring.training.boot.server.DTOs.author;

import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;

import java.util.List;

public record AuthorResponseDto(Long id,
                                String name,
                                String bio,
                                String nationality,
                                String photo,
                                List<GenreResponseDto> genres) {
}
