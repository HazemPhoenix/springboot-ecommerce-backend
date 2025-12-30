package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.BookDto;
import io.spring.training.boot.server.models.Book;

public class BookMapper {
    public static BookDto toDto(Book book){
        return new BookDto(book.getId(), book.getTitle(), book.getDescription(), book.getPrice(), book.getNumberOfPages(), book.getImage());
    }
}
