package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.*;
import io.spring.training.boot.server.models.embeddables.ReviewId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Set;

@DataJpaTest
public class ReviewRepoTest {
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AuthorRepo authorRepo;
    @Autowired
    private GenreRepo genreRepo;
    @Autowired
    private ReviewRepo reviewRepo;

    @Test
    public void givenExistingBooksWithReviews_whenFindByIdBookIdAndKeywordIsCalled_thenReturnReviewsForMatchingBooks(){
        // Arrange
        User user1 = userRepo.save(User.builder().username("test").email("test@test.com").password("test").build());
        User user2 = userRepo.save(User.builder().username("test2").email("test2@test.com").password("test").build());
        User user3 = userRepo.save(User.builder().username("test3").email("test3@test.com").password("test").build());

        Genre g = new Genre("Horror");
        Genre genre = genreRepo.save(g);

        Author a = Author.builder().name("test").bio("test").nationality("test").photo("test").genres(Set.of(genre)).build();
        Author author = authorRepo.save(a);

        Book b1 = Book.builder()
                .title("book 1")
                .description("test")
                .price(BigDecimal.TEN)
                .numberOfPages(300)
                .stock(20)
                .genres(Set.of(genre))
                .authors(Set.of(author))
                .image("test.png").build();

        Book book1 = bookRepo.save(b1);

        Book b2 = Book.builder()
                .title("book 2")
                .description("test")
                .price(BigDecimal.TEN)
                .numberOfPages(300)
                .stock(20)
                .genres(Set.of(genre))
                .authors(Set.of(author))
                .image("test.png").build();

        Book book2 = bookRepo.save(b2);

        ReviewId reviewId1 = new ReviewId(user1.getId(), book1.getId());
        ReviewId reviewId2 = new ReviewId(user2.getId(), book1.getId());
        ReviewId reviewId3 = new ReviewId(user3.getId(), book1.getId());

        ReviewId reviewId4 = new ReviewId(user2.getId(), book2.getId());

        String keyword = "butterfly";

        Review rev1 = Review.builder().book(book1).title("for book 1, contains keyword: " + keyword).content("the content does not contain it").id(reviewId1).rating(5).build();
        Review rev2 = Review.builder().book(book1).title("for book 1, title does not contain key word").content("but content does: " + keyword).id(reviewId2).rating(4).build();
        Review rev3 = Review.builder().book(book1).title("for book 1, no keyword").content("not here either").id(reviewId3).rating(1).build();

        Review rev4 = Review.builder().book(book2).title("for book 2, contains keyword but is for another book: " + keyword).content("hello there!").id(reviewId4).rating(1).build();
        reviewRepo.saveAll(Set.of(rev1, rev2, rev3, rev4));

        // Act
        Page<Review> reviews = reviewRepo.findById_BookIdAndKeyword(Pageable.unpaged(), book1.getId(), keyword);
        assertThat(reviews.getSize()).isEqualTo(2);
        assertThat(reviews.getContent()).contains(rev1, rev2);
        assertThat(reviews.getContent()).doesNotContain(rev3, rev4);
    }
}
