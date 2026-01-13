package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.review.ReviewRequestDto;
import io.spring.training.boot.server.DTOs.review.ReviewResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.models.Review;
import io.spring.training.boot.server.models.embeddables.ReviewId;
import io.spring.training.boot.server.services.BookService;
import io.spring.training.boot.server.utils.mappers.ReviewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookReviewController.class)
public class BookReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageProperties storageProperties;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Review> reviews;

    private final String baseUrl = "/api/v1/books/{bookId}/reviews";


    @BeforeEach
    public void setup() {
        Author a1 = Author.builder()
                .id(1L)
                .name("first author")
                .bio("first bio")
                .nationality("first nat")
                .genres(Set.of(new Genre("Horror")))
                .photo("first.png").build();

        Author a2 = Author.builder()
                .id(2L)
                .name("second author")
                .bio("second bio")
                .nationality("second nat")
                .genres(Set.of(new Genre("Drama")))
                .photo("second.png").build();

        Book b1 = Book.builder()
                .id(1L)
                .title("Book One")
                .description("Description One")
                .price(new BigDecimal("29.99"))
                .numberOfPages(300)
                .stock(10)
                .image("book-one.png")
                .authors(Set.of(a1))
                .genres(Set.of(new Genre("Horror")))
                .build();

        Book b2 = Book.builder()
                .id(2L)
                .title("Book Two")
                .description("Description Two")
                .price(new BigDecimal("39.99"))
                .numberOfPages(400)
                .stock(5)
                .image("book-two.png")
                .authors(Set.of(a2))
                .genres(Set.of(new Genre("Drama")))
                .build();

        ReviewId reviewId1 = new ReviewId(1L, 1L);
        Review r1 = Review.builder()
                .id(reviewId1)
                .rating(5)
                .title("Great Book")
                .content("I really enjoyed reading this book.")
                .edited(false)
                .book(b1)
                .build();

        ReviewId reviewId2 = new ReviewId(2L, 2L);
        Review r2 = Review.builder()
                .id(reviewId2)
                .rating(4)
                .title("Good Read")
                .content("This book was quite engaging.")
                .edited(false)
                .book(b2)
                .build();

        ReviewId reviewId3 = new ReviewId(3L, 1L);
        Review r3 = Review.builder()
                .id(reviewId3)
                .rating(3)
                .title("Average Book")
                .content("It was an okay read, nothing special.")
                .edited(false)
                .book(b1)
                .build();

        reviews = List.of(r1, r2, r3);
    }

    @Test
    public void givenValidBookIdAndNoKeyword_whenGetReviewsForBookIsCalled_thenReturnsPaginatedReviews() throws Exception {
        // arrange
        Long bookId = 1L;
        var pageable = PageRequest.of(0, 20);
        var response = new PageImpl<>(
                reviews.stream().filter(review -> Objects.equals(review.getId().getBookId(), bookId)).map(ReviewMapper::toReviewResponseDto).toList(),
                pageable,
                2
        );

        when(bookService.getReviewsForBook(eq(bookId), any(Pageable.class), isNull())).thenReturn(response);

        // act and assert
        mockMvc.perform(get(baseUrl, bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Great Book"))
                .andExpect(jsonPath("$.content[1].title").value("Average Book"));

        verify(bookService).getReviewsForBook(eq(bookId), any(Pageable.class), isNull());
    }

    @Test
    public void givenValidBookIdAndKeyword_whenGetReviewsForBookIsCalled_thenReturnsFilteredPaginatedReviews() throws Exception {
        // arrange
        Long bookId = 1L;
        String keyword = "okay";
        var pageable = PageRequest.of(0, 20);
        var response = new PageImpl<>(
                reviews.stream()
                        .filter(review -> Objects.equals(review.getId().getBookId(), bookId))
                        .filter(review -> review.getTitle().contains(keyword) || review.getContent().contains(keyword))
                        .map(ReviewMapper::toReviewResponseDto)
                        .toList(),
                pageable,
                1
        );

        when(bookService.getReviewsForBook(eq(bookId), any(Pageable.class), eq(keyword))).thenReturn(response);

        // act and assert
        mockMvc.perform(get(baseUrl, bookId)
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Average Book"));

        verify(bookService).getReviewsForBook(eq(bookId), any(Pageable.class), eq(keyword));
    }

    @Test
    public void givenInvalidBookId_whenGetReviewsForBookIsCalled_thenReturnsNotFound() throws Exception {
        // arrange
        Long bookId = 10L;

        when(bookService.getReviewsForBook(eq(bookId), any(Pageable.class), isNull()))
                .thenThrow(BookNotFoundException.class);

        // act and assert
        mockMvc.perform(get(baseUrl, bookId))
                .andExpect(status().isNotFound());

        verify(bookService).getReviewsForBook(eq(bookId), any(Pageable.class), isNull());
    }

    @Test
    public void givenValidBookIdWithoutUserId_whenDeleteReviewIsCalled_thenCallsUserVersionAndReturnsNoContent() throws Exception {
        // arrange
        Long bookId = 1L;
        doNothing().when(bookService).deleteReviewForUser(anyLong());

        // act and assert
        mockMvc.perform(delete(baseUrl, bookId))
                .andExpect(status().isNoContent());

        verify(bookService).deleteReviewForUser(eq(bookId));
        verify(bookService, never()).deleteReviewForAdmin(anyLong(), anyLong());
    }
}
