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
    private Book book;
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

        book = Book.builder()
                .id(1L)
                .title("test title")
                .description("test desc")
                .price(BigDecimal.valueOf(29.99))
                .numberOfPages(300)
                .stock(30)
                .image("image.png")
                .authors(new HashSet<>(authors))
                .genres(genres).build();

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

        when(bookRepo.save(any(Book.class))).thenReturn(book);

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
        when(bookWithStats.getBook()).thenReturn(book);
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

}
