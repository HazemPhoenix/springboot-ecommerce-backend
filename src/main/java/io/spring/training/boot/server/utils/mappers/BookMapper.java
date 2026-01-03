package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.AuthorResponseDto;
import io.spring.training.boot.server.DTOs.BookResponseDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.models.Book;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

public class BookMapper {
    public static BookResponseDto toBookResponseDto(Book book){
        List<AuthorResponseDto> authorResponseDtos = book.getAuthors().stream().map(AuthorMapper::toAuthorResponseDto).toList();
        String bookImage = ServletUriComponentsBuilder.fromCurrentContextPath().build() + "/uploads/" + book.getImage();
        return new BookResponseDto(book.getId(), book.getTitle(), book.getDescription(), book.getPrice(), book.getNumberOfPages(), bookImage, authorResponseDtos);
    }

    public static Book fromBookRequestDto(BookRequestDto bookRequestDto){
        return new Book(bookRequestDto.title(), bookRequestDto.description(), bookRequestDto.price(), bookRequestDto.numberOfPages());
    }
}
