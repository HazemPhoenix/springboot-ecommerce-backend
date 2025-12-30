package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.models.Book;

public class BookMapper {
    public static BookDto toDto(Book book){
        return new BookDto(book.getId(), book.getTitle(), book.getDescription(), book.getPrice(), book.getNumberOfPages(), book.getImage());
    }

    public static Book fromDto(BookRequestDto bookRequestDto){
        return new Book(bookRequestDto.title(), bookRequestDto.description(), bookRequestDto.price(), bookRequestDto.numberOfPages(), bookRequestDto.image());
    }
}
