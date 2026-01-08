package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.DTOs.book.*;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.DTOs.review.ReviewResponseDto;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.projections.BookWithStats;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

public class BookMapper {
    public static BookCreationResponseDto toBookResponseDto(Book book){
        List<AuthorResponseDto> authorResponseDtos = book.getAuthors().stream().map(AuthorMapper::toAuthorResponseDto).toList();
        List<GenreResponseDto> genreResponseDtos = book.getGenres().stream().map(GenreMapper::toGenreResponseDto).toList();
        List<ReviewResponseDto> reviewResponseDtos = null;
        if(book.getReviews() != null) {
          reviewResponseDtos = book.getReviews().stream().map(ReviewMapper::toReviewResponseDto).toList();
        }
//        String bookImage = ServletUriComponentsBuilder.fromCurrentContextPath().build() + "/uploads/" + (book.getImage() != null ? book.getImage() : "");
        String bookImage = "http://localhost:8080//uploads/" + book.getImage();
        return new BookCreationResponseDto(book.getId(), book.getTitle(), book.getDescription(), book.getPrice(), book.getNumberOfPages(), book.getStock(),bookImage, authorResponseDtos, genreResponseDtos, reviewResponseDtos);
    }

    public static BookResponseWithStats toBookResponseWithStats(BookWithStats bookWithStats){
        List<AuthorResponseDto> authorResponseDtos = bookWithStats.getBook().getAuthors().stream().map(AuthorMapper::toAuthorResponseDto).toList();
        List<GenreResponseDto> genreResponseDtos = bookWithStats.getBook().getGenres().stream().map(GenreMapper::toGenreResponseDto).toList();
        List<ReviewResponseDto> reviewResponseDtos = null;
        if(bookWithStats.getBook().getReviews() != null) {
            reviewResponseDtos = bookWithStats.getBook().getReviews().stream().map(ReviewMapper::toReviewResponseDto).toList();
        }
        String bookImage = ServletUriComponentsBuilder.fromCurrentContextPath().build() + "/uploads/" + (bookWithStats.getBook().getImage() != null ? bookWithStats.getBook().getImage() : "");
//        String bookImage = "http://localhost:8080//uploads/" + bookWithStats.getBook().getImage();
        return new BookResponseWithStats(bookWithStats.getBook().getId(),
                bookWithStats.getBook().getTitle(),
                bookWithStats.getBook().getDescription(),
                bookWithStats.getBook().getPrice(),
                bookWithStats.getBook().getNumberOfPages(),
                bookWithStats.getBook().getStock(),
                bookImage,
                bookWithStats.getTotalReviews(),
                bookWithStats.getAverageRating(),
                authorResponseDtos,
                genreResponseDtos,
                reviewResponseDtos);
    }

    public static BookSummaryDto toBookSummaryDto(Book book, int totalReviews, double averageRating) {
        List<String> authorDtos = book.getAuthors().stream().map(Author::getName).toList();
        String bookImage = ServletUriComponentsBuilder.fromCurrentContextPath().build() + "/uploads/" + (book.getImage() != null ? book.getImage() : "");
//        String bookImage = "http://localhost:8080//uploads/" + book.getImage();
        return new BookSummaryDto(book.getId(), book.getTitle(), book.getPrice(), bookImage, authorDtos, totalReviews, averageRating);
    }

    public static BookOrderSummaryDto toBookOrderSummaryDto(Book b){
        return new BookOrderSummaryDto(b.getId(), b.getTitle(), b.getPrice());
    }

    public static Book fromBookRequestDto(BookRequestDto bookRequestDto){
        return new Book(bookRequestDto.title(), bookRequestDto.description(), bookRequestDto.price(), bookRequestDto.numberOfPages(), bookRequestDto.stock());
    }
}
