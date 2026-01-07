package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.DTOs.book.BookCreationResponseDto;
import io.spring.training.boot.server.DTOs.book.BookRequestDto;
import io.spring.training.boot.server.DTOs.book.BookResponseWithStats;
import io.spring.training.boot.server.DTOs.book.BookSummaryDto;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.DTOs.review.ReviewRequestDto;
import io.spring.training.boot.server.DTOs.review.ReviewResponseDto;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.exceptions.ReviewNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.models.Review;
import io.spring.training.boot.server.models.embeddables.ReviewId;
import io.spring.training.boot.server.models.projections.BookWithStats;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.repositories.ReviewRepo;
import io.spring.training.boot.server.services.implementations.AuthorServiceImpl;
import io.spring.training.boot.server.services.implementations.BookServiceImpl;
import io.spring.training.boot.server.services.implementations.GenreServiceImpl;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {
    @Mock
    private BookRepo bookRepo;
    @Mock
    private AuthorServiceImpl authorService;
    @Mock
    private ImageStorageService imageStorageService;
    @Mock
    private GenreServiceImpl genreService;
    @Mock
    private ReviewRepo reviewRepo;

    @InjectMocks
    private BookServiceImpl bookService;


    private List<Author> authors;
    private List<Book> books;
    private Set<Genre> genres;

    @BeforeEach
    public void setup(){
        Author firstAuthor = Author.builder()
                .id(1L)
                .name("first author")
                .bio("first bio")
                .nationality("first nat")
                .genres(Set.of(new Genre("horror")))
                .photo("first photo.png").build();

        Author secondAuthor = Author.builder()
                .id(2L)
                .name("second author")
                .bio("second bio")
                .nationality("second nat")
                .genres(Set.of(new Genre("horror")))
                .photo("second photo.png").build();

        authors = List.of(firstAuthor, secondAuthor);

        genres = Set.of(new Genre("Horror"), new Genre("Drama"));

        Book book1 = Book.builder()
                .id(1L)
                .title("first book title")
                .description("first book desc")
                .price(BigDecimal.valueOf(29.99))
                .numberOfPages(300)
                .stock(30)
                .image("image.png")
                .authors(new HashSet<>(authors))
                .genres(genres).build();

        Book book2 = Book.builder()
                .id(1L)
                .title("second book title")
                .description("second book desc")
                .price(BigDecimal.valueOf(29.99))
                .numberOfPages(300)
                .stock(30)
                .image("image2.png")
                .authors(new HashSet<>(authors))
                .genres(genres).build();

        books = List.of(book1, book2);
    }

    @Test
    public void givenValidBookRequestDtoAndImage_whenCreateBookIsCalled_thenReturnCorrectBookCreationResponseDto(){
        // Arrange
        when(authorService.findAuthorsByIds(any(Set.class))).thenReturn(new HashSet<>(authors));
        when(genreService.findGenresByIds(any(Set.class))).thenReturn(genres);
        when(imageStorageService.storeBookImage(any(MultipartFile.class))).thenReturn("image.png");

        Set<Long> authorIds = Set.of(1L, 2L);
        Set<Long> genreIds = Set.of(1L, 2L);
        BookRequestDto bookRequestDto = new BookRequestDto("test title", "test desc", BigDecimal.valueOf(29.99), 300, 30, authorIds, genreIds);
        MultipartFile image = new MockMultipartFile("image.png", new byte[]{1,2,3});

        when(bookRepo.save(any(Book.class))).thenReturn(books.get(0));

        // Act
        BookCreationResponseDto bookCreationResponseDto = bookService.createBook(bookRequestDto, image);

        // Assert
        verify(authorService).findAuthorsByIds(authorIds);
        verify(genreService).findGenresByIds(genreIds);
        verify(imageStorageService).storeBookImage(image);
        verify(bookRepo).save(any(Book.class));

        assertThat(bookCreationResponseDto).isNotNull();
        assertThat(bookCreationResponseDto.title()).isEqualTo("first book title");
        assertThat(bookCreationResponseDto.description()).isEqualTo("first book desc");
        assertThat(bookCreationResponseDto.price()).isEqualTo(BigDecimal.valueOf(29.99));
        assertThat(bookCreationResponseDto.numberOfPages()).isEqualTo(300);
        assertThat(bookCreationResponseDto.stock()).isEqualTo(30);
        assertThat(bookCreationResponseDto.image()).isEqualTo("http://localhost:8080//uploads/image.png");
        assertThat(bookCreationResponseDto.authors().stream().map(AuthorResponseDto::name).toList()).containsAll(authors.stream().map(Author::getName).toList());
        assertThat(bookCreationResponseDto.genres().stream().map(GenreResponseDto::name).toList()).isEqualTo(genres.stream().map(Genre::getName).toList());
    }
    @Test
    public void givenValidId_whenFindBookByIdIsCalled_thenReturnCorrectBookResponseWithStats() {
        // Arrange
        BookWithStats bookWithStats = mock(BookWithStats.class);
        when(bookWithStats.getBook()).thenReturn(books.get(0));
        when(bookWithStats.getTotalReviews()).thenReturn(5);
        when(bookWithStats.getAverageRating()).thenReturn(4.5);

        when(bookRepo.findBookByIdWithStats(1L)).thenReturn(Optional.of(bookWithStats));

        // Act
        BookResponseWithStats response = bookService.findBookById(1L);

        //Assert
        verify(bookRepo).findBookByIdWithStats(1L);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("first book title");
        assertThat(response.totalReviews()).isEqualTo(5L);
        assertThat(response.averageRating()).isEqualTo(4.5);
    }

    @Test
    public void givenInvalidId_whenFindBookByIdIsCalled_thenThrowBookNotFoundException() {
        // Arrange
        when(bookRepo.findBookByIdWithStats(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookService.findBookById(10L))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepo).findBookByIdWithStats(10L);
    }


    @Test
    public void givenKeyword_whenGetAllBooksIsCalled_thenReturnFilteredPageOfBookSummaryDto() {
        // Arrange
        PageRequest pageable = PageRequest.of(1, 10);
        String keyword = "first";

        BookWithStats bookWithStats = mock(BookWithStats.class);

        when(bookWithStats.getBook()).thenReturn(books.get(0));
        when(bookWithStats.getTotalReviews()).thenReturn(1);
        when(bookWithStats.getAverageRating()).thenReturn(5.0);

        Page<BookWithStats> page = new PageImpl<>(List.of(bookWithStats));

        when(bookRepo.findAllBooksWithStatsContaining(pageable, keyword)).thenReturn(page);

        // Act
        Page<BookSummaryDto> result = bookService.getAllBooks(pageable, keyword);


        // Assert
        verify(bookRepo).findAllBooksWithStatsContaining(pageable, keyword);
        assertThat(result).hasSize(1);
        assertThat(result.getContent().stream().map(BookSummaryDto::title).toList()).containsAll(List.of(books.get(0).getTitle()));
        assertThat(result.getContent().stream().map(BookSummaryDto::title).toList()).doesNotContain(books.get(1).getTitle());

    }

    @Test
    public void givenNoKeyword_whenGetAllBooksIsCalled_thenReturnPageOfBookSummaryDto() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);

        BookWithStats stats1 = mock(BookWithStats.class);
        when(stats1.getBook()).thenReturn(books.get(0));
        when(stats1.getTotalReviews()).thenReturn(5);
        when(stats1.getAverageRating()).thenReturn(4.5);

        BookWithStats stats2 = mock(BookWithStats.class);
        when(stats2.getBook()).thenReturn(books.get(1));
        when(stats2.getTotalReviews()).thenReturn(2);
        when(stats2.getAverageRating()).thenReturn(3.0);

        Page<BookWithStats> page = new PageImpl<>(List.of(stats1, stats2));

        when(bookRepo.findAllBooksWithStats(pageable)).thenReturn(page);

        // Act
        Page<BookSummaryDto> result = bookService.getAllBooks(pageable, null);

        // Assert
        verify(bookRepo).findAllBooksWithStats(pageable);
        assertThat(result).hasSize(2);
        assertThat(result.getContent().stream().map(BookSummaryDto::title).toList())
                .containsExactlyInAnyOrder("first book title", "second book title");
    }

    @Test
    public void givenValidIdAndRequest_whenUpdateBookByIdIsCalled_thenReturnUpdatedBookResponse() {
        // Arrange
        Long id = 1L;
        Book oldBook = books.get(0); // has "image.png"

        Set<Long> authorIds = Set.of(1L, 2L);
        Set<Long> genreIds = Set.of(1L, 2L);
        BookRequestDto bookRequestDto = new BookRequestDto("updated title", "updated desc", BigDecimal.valueOf(50.00), 500, 50, authorIds, genreIds);
        MultipartFile newImage = new MockMultipartFile("new_image.png", new byte[]{1,2,3});

        when(bookRepo.findById(id)).thenReturn(Optional.of(oldBook));
        when(authorService.findAuthorsByIds(authorIds)).thenReturn(new HashSet<>(authors));
        when(genreService.findGenresByIds(genreIds)).thenReturn(genres);
        when(imageStorageService.storeBookImage(newImage)).thenReturn("new_image.png");

        when(bookRepo.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        BookCreationResponseDto response = bookService.updateBookById(id, bookRequestDto, newImage);

        // Assert
        verify(imageStorageService).deleteBookImage("image.png");
        verify(bookRepo).save(any(Book.class));
        assertThat(response.title()).isEqualTo("updated title");
        assertThat(response.image()).contains("new_image.png");
    }

    @Test
    public void givenInvalidId_whenUpdateBookByIdIsCalled_thenThrowBookNotFoundException() {
        // Arrange
        Long id = 10L;
        BookRequestDto bookRequestDto = new BookRequestDto("title", "desc", BigDecimal.TEN, 100, 10, Set.of(), Set.of());

        when(bookRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookService.updateBookById(id, bookRequestDto, null))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepo).findById(id);
        verify(bookRepo, never()).save(any(Book.class));
    }

    @Test
    public void givenValidId_whenDeleteBookByIdIsCalled_thenDeleteImageAndBook() {
        // Arrange
        Long id = 1L;
        Book bookToDelete = books.get(0); // Has "image.png"
        when(bookRepo.findById(id)).thenReturn(Optional.of(bookToDelete));

        // Act
        bookService.deleteBookById(id);

        // Assert
        verify(imageStorageService).deleteBookImage("image.png");
        verify(bookRepo).delete(bookToDelete);
    }

    @Test
    public void givenValidBookIdAndReviewRequest_whenCreateReviewForBookIsCalled_thenReturnReviewResponse() {
        // Arrange
        Long bookId = 1L;
        ReviewRequestDto reviewRequest = new ReviewRequestDto(5, "great book", "such a great book!");

        when(bookRepo.existsById(bookId)).thenReturn(true);
        when(reviewRepo.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ReviewResponseDto response = bookService.createReviewForBook(bookId, reviewRequest);

        // Assert
        verify(reviewRepo).save(any(Review.class));
        assertThat(response.rating()).isEqualTo(5);
        assertThat(response.content()).isEqualTo("such a great book!");
    }

    @Test
    public void givenInvalidBookId_whenCreateReviewForBookIsCalled_thenThrowBookNotFoundException() {
        // Arrange
        Long bookId = 999L;
        ReviewRequestDto reviewRequest = new ReviewRequestDto(5, "great book", "such a great book!");

        when(bookRepo.existsById(bookId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bookService.createReviewForBook(bookId, reviewRequest))
                .isInstanceOf(BookNotFoundException.class);

        verify(reviewRepo, never()).save(any(Review.class));
    }

    @Test
    public void givenValidIds_whenUpdateReviewForBookIsCalled_thenReturnUpdatedReviewResponse() {
        // Arrange
        Long bookId = 1L;
        ReviewRequestDto reviewRequest = new ReviewRequestDto(4, "updated comment", "i updated to content lol");
        ReviewId reviewId = new ReviewId(17L, bookId);

        when(bookRepo.existsById(bookId)).thenReturn(true);
        when(reviewRepo.existsById(reviewId)).thenReturn(true);
        when(reviewRepo.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ReviewResponseDto response = bookService.updateReviewForBook(bookId, reviewRequest);

        // Assert
        assertThat(response.rating()).isEqualTo(4);
        assertThat(response.title()).isEqualTo("updated comment");
        assertThat(response.content()).isEqualTo("i updated to content lol");
        verify(reviewRepo).save(any(Review.class));
        verify(reviewRepo).existsById(reviewId);
        verify(bookRepo).existsById(bookId);
    }

    @Test
    public void givenMissingReview_whenUpdateReviewForBookIsCalled_thenThrowReviewNotFoundException() {
        // Arrange
        Long bookId = 1L;
        ReviewRequestDto reviewRequest = new ReviewRequestDto(4, "updated comment", "i updated to content lol");
        ReviewId reviewId = new ReviewId(17L, bookId);

        when(bookRepo.existsById(bookId)).thenReturn(true);
        when(reviewRepo.existsById(reviewId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bookService.updateReviewForBook(bookId, reviewRequest))
                .isInstanceOf(ReviewNotFoundException.class);

        verify(reviewRepo, never()).save(any(Review.class));
    }

    @Test
    public void givenNoKeyword_whenGetReviewsForBookIsCalled_thenReturnPageOfReviews() {
        // Arrange
        Long bookId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);
        Review review = Review.builder()
                .id(new ReviewId(17L, bookId))
                .rating(5)
                .content("nice")
                .build();

        when(reviewRepo.findById_BookId(pageable, bookId)).thenReturn(new PageImpl<>(List.of(review)));

        // Act
        Page<ReviewResponseDto> result = bookService.getReviewsForBook(bookId, pageable, null);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).content()).isEqualTo("nice");
        verify(reviewRepo).findById_BookId(pageable, bookId);
        verify(reviewRepo, never()).findById_BookIdAndKeyword(eq(pageable), eq(bookId), any(String.class));
    }

    @Test
    public void givenKeyword_whenGetReviewsForBookIsCalled_thenReturnFilteredPageOfReviews() {
        // Arrange
        Long bookId = 1L;
        String keyword = "nice";
        PageRequest pageable = PageRequest.of(0, 10);

        Review review1 = Review.builder()
                .id(new ReviewId(17L, bookId))
                .rating(5)
                .content("nice")
                .build();

        Review review2 = Review.builder()
                .id(new ReviewId(18L, bookId))
                .rating(0)
                .content("bad")
                .build();

        when(reviewRepo.findById_BookIdAndKeyword(pageable, bookId, keyword)).thenReturn(new PageImpl<>(List.of(review1)));

        // Act
        Page<ReviewResponseDto> result = bookService.getReviewsForBook(bookId, pageable, keyword);

        // Assert
        verify(reviewRepo).findById_BookIdAndKeyword(pageable, bookId, keyword);
        verify(reviewRepo, never()).findById_BookId(any(Pageable.class), any(Long.class));
        assertThat(result).hasSize(1);
        assertThat(result.map(ReviewResponseDto::content)).contains(review1.getContent());
        assertThat(result.map(ReviewResponseDto::content)).doesNotContain(review2.getContent());
    }

}
