package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.DTOs.book.BookCreationResponseDto;
import io.spring.training.boot.server.DTOs.book.BookRequestDto;
import io.spring.training.boot.server.DTOs.book.BookResponseWithStats;
import io.spring.training.boot.server.DTOs.book.BookSummaryDto;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import static org.assertj.core.api.Assertions.*;

import java.awt.print.Pageable;
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
        assertThat(bookCreationResponseDto.title()).isEqualTo("test title");
        assertThat(bookCreationResponseDto.description()).isEqualTo("test desc");
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
        assertThat(response.title()).isEqualTo("test title");
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

}
