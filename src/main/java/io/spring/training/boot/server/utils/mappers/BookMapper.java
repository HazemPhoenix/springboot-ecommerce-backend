package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.AuthorDto;
import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;

import java.security.Security;
import java.util.List;
import java.util.Set;

public class BookMapper {
    public static BookDto toBookDto(Book book){
        List<AuthorDto> authorDtos = book.getAuthors().stream().map(AuthorMapper::toAuthorDto).toList();
        return new BookDto(book.getId(), book.getTitle(), book.getDescription(), book.getPrice(), book.getNumberOfPages(), book.getImage(), authorDtos);
    }

    public static Book fromBookRequestDto(BookRequestDto bookRequestDto){
        return new Book(bookRequestDto.title(), bookRequestDto.description(), bookRequestDto.price(), bookRequestDto.numberOfPages());
    }
}
