package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.AuthorDto;
import io.spring.training.boot.server.DTOs.AuthorRequestDto;
import io.spring.training.boot.server.models.Author;

public class AuthorMapper {
    public static AuthorDto toAuthorDto(Author author){
        return new AuthorDto(author.getId(), author.getName(), author.getBio(), author.getNationality(), author.getPhoto());
    }

    public static Author fromAuthorRequestDto(AuthorRequestDto authorRequestDto){
        return new Author(authorRequestDto.name(), authorRequestDto.bio(), authorRequestDto.nationality(), authorRequestDto.photo());
    }
}
