package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.AuthorDto;
import io.spring.training.boot.server.DTOs.AuthorRequestDto;
import io.spring.training.boot.server.models.Author;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class AuthorMapper {
    public static AuthorDto toAuthorDto(Author author){
        String imageName = ServletUriComponentsBuilder.fromCurrentContextPath().build() + "/uploads/" + author.getPhoto();
        return new AuthorDto(author.getId(), author.getName(), author.getBio(), author.getNationality(), imageName);
    }

    public static Author fromAuthorRequestDto(AuthorRequestDto authorRequestDto){
        return new Author(authorRequestDto.name(), authorRequestDto.bio(), authorRequestDto.nationality());
    }
}
