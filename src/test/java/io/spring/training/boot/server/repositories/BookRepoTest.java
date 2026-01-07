package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.*;
import io.spring.training.boot.server.models.embeddables.ReviewId;
import io.spring.training.boot.server.models.projections.BookWithStats;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class BookRepoTest {
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private ReviewRepo reviewRepo;
    @Autowired
    private AuthorRepo authorRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GenreRepo genreRepo;

    private User user1;
    private User user2;
    private Genre genre;
    private Author author;
    private Book book;
    @BeforeEach
    public void setup(){
        user1 = userRepo.save(User.builder().username("test").email("test@test.com").password("test").build());
        user2 = userRepo.save(User.builder().username("test2").email("test2@test.com").password("test").build());

        Genre g = new Genre("Horror");
        genre = genreRepo.save(g);

        Author a = Author.builder().name("test").bio("test").nationality("test").photo("test").genres(Set.of(genre)).build();
        author = authorRepo.save(a);
    }

    @Test
    void givenExistingBookWithReviews_whenFindAllBooksWithStatsIsCalled_thenReturnsCorrectStats() {
        // Arrange
        Book b = Book.builder()
                .title("test")
                .description("test")
                .price(BigDecimal.TEN)
                .numberOfPages(300)
                .stock(20)
                .genres(Set.of(genre))
                .authors(Set.of(author))
                .image("test.png").build();

        book = bookRepo.save(b);

        ReviewId reviewId1 = new ReviewId(user1.getId(), book.getId());
        ReviewId reviewId2 = new ReviewId(user2.getId(), book.getId());

        Review rev1 = Review.builder().book(book).id(reviewId1).rating(5).build();
        Review rev2 = Review.builder().book(book).id(reviewId2).rating(4).build();

        reviewRepo.saveAll(List.of(rev1, rev2));

        // Act
        BookWithStats bookWithStats = bookRepo.findAllBooksWithStats(Pageable.unpaged()).getContent().getFirst();

        // Assert
        assertThat(bookWithStats).isNotNull();
        assertThat(bookWithStats.getBook().getTitle()).isEqualTo("test");
        assertThat(bookWithStats.getAverageRating()).isEqualTo(4.5);
        assertThat(bookWithStats.getTotalReviews()).isEqualTo(2);
    }

    @Test
    public void givenExistingBookWithNoReviews_whenFindAllBooksWithStatsIsCalled_thenReturnsCorrectStats() {
        // Arrange
        Book b = Book.builder()
                .title("test")
                .description("test")
                .price(BigDecimal.TEN)
                .numberOfPages(300)
                .stock(20)
                .genres(Set.of(genre))
                .authors(Set.of(author))
                .image("test.png").build();

        book = bookRepo.save(b);

        // Act
        BookWithStats bookWithStats = bookRepo.findAllBooksWithStats(Pageable.unpaged()).getContent().getFirst();

        // Assert
        assertThat(bookWithStats).isNotNull();
        assertThat(bookWithStats.getTotalReviews()).isEqualTo(0);
        assertThat(bookWithStats.getAverageRating()).isEqualTo(0.0);
    }

}