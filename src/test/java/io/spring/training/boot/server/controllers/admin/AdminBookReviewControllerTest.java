package io.spring.training.boot.server.controllers.admin;

import io.spring.training.boot.server.DTOs.review.ReviewRequestDto;
import io.spring.training.boot.server.DTOs.review.ReviewResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.models.Review;
import io.spring.training.boot.server.models.embeddables.ReviewId;
import io.spring.training.boot.server.security.filters.JwtFilter;
import io.spring.training.boot.server.security.services.JwtService;
import io.spring.training.boot.server.security.services.UserDetailsServiceImpl;
import io.spring.training.boot.server.services.BookService;
import io.spring.training.boot.server.utils.mappers.ReviewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookReviewController.class)
public class AdminBookReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageProperties storageProperties;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private List<Review> reviews;

    private final String baseUrl = "/api/v1/admin/books/{bookId}/reviews";


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
    public void givenValidBookIdAndUserId_whenDeleteReviewIsCalled_thenCallsAdminVersionAndReturnsNoContent() throws Exception {
        // arrange
        Long bookId = 1L;
        Long userId = 1L;

        doNothing().when(bookService).deleteReviewForAdmin(anyLong(), anyLong());

        // act and assert
        mockMvc.perform(delete(baseUrl + "/{userId}", bookId, userId)
                        .queryParam("userId", String.valueOf(userId)))
                .andExpect(status().isNoContent());

        verify(bookService).deleteReviewForAdmin(eq(bookId), eq(userId));
        verify(bookService, never()).deleteReviewForUser(anyLong());
    }
}
