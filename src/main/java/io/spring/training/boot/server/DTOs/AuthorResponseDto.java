package io.spring.training.boot.server.DTOs;

import java.util.List;

public record AuthorResponseDto(Long id,
                                String name,
                                String bio,
                                String nationality,
                                String photo,
                                List<GenreResponseDto> genres) {
}
