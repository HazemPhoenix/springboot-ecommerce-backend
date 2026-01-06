package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.DTOs.author.AuthorRequestDto;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.DTOs.author.SimpleAuthorDto;
import io.spring.training.boot.server.models.Author;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

public class AuthorMapper {
    public static AuthorResponseDto toAuthorResponseDto(Author author){
        String imageName = ServletUriComponentsBuilder.fromCurrentContextPath().build() + "/uploads/" + (author.getPhoto() !=  null ? author.getPhoto() : "");
        List<GenreResponseDto> genreResponseDtos = author.getGenres().stream().map(GenreMapper::toGenreResponseDto).toList();
        return new AuthorResponseDto(author.getId(), author.getName(), author.getBio(), author.getNationality(), imageName, genreResponseDtos);
    }

    public static Author fromAuthorRequestDto(AuthorRequestDto authorRequestDto){
        return new Author(authorRequestDto.name(), authorRequestDto.bio(), authorRequestDto.nationality());
    }

    public static SimpleAuthorDto toSimpleAuthorDto(Author author) {
        return new SimpleAuthorDto(author.getName());
    }
}
