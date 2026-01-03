package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.*;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookMapper {
    public static BookResponseDto toBookResponseDto(Book book){
        List<AuthorResponseDto> authorResponseDtos = book.getAuthors().stream().map(AuthorMapper::toAuthorResponseDto).toList();
        List<GenreResponseDto> genreResponseDtos = book.getGenres().stream().map(GenreMapper::toGenreResponseDto).toList();
        String bookImage = ServletUriComponentsBuilder.fromCurrentContextPath().build() + "/uploads/" + (book.getImage() != null ? book.getImage() : "");
        return new BookResponseDto(book.getId(), book.getTitle(), book.getDescription(), book.getPrice(), book.getNumberOfPages(), bookImage, authorResponseDtos, genreResponseDtos);
    }

    public static BookSummaryDto toBookSummaryDto(Book book) {
        List<String> authorDtos = book.getAuthors().stream().map(Author::getName).toList();
        String bookImage = ServletUriComponentsBuilder.fromCurrentContextPath().build() + "/uploads/" + (book.getImage() != null ? book.getImage() : "");
        return new BookSummaryDto(book.getId(), book.getTitle(), book.getPrice(), bookImage, authorDtos);
    }

    public static Book fromBookRequestDto(BookRequestDto bookRequestDto){
        return new Book(bookRequestDto.title(), bookRequestDto.description(), bookRequestDto.price(), bookRequestDto.numberOfPages());
    }
}
