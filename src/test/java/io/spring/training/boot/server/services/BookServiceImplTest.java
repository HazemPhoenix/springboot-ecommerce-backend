package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.DTOs.book.BookCreationResponseDto;
import io.spring.training.boot.server.DTOs.book.BookRequestDto;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
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
    }

    @Test
    public void givenValidBookRequestDtoAndImage_whenCreateBookIsCalled_thenReturnCorrectBookCreationResponseDto(){
        // Arrange
        when(authorService.findAuthorsByIds(any(Set.class))).thenReturn(new HashSet<>(authors));
        Set<Genre> genres = Set.of(new Genre("Horror"), new Genre("Drama"));
        when(genreService.findGenresByIds(any(Set.class))).thenReturn(genres);
        when(imageStorageService.storeBookImage(any(MultipartFile.class))).thenReturn("image.png");
        Book book = Book.builder()
                        .id(1L)
                        .title("test title")
                        .description("test desc")
                        .price(BigDecimal.valueOf(29.99))
                        .numberOfPages(300)
                        .stock(30)
                        .image("image.png")
                        .authors(new HashSet<>(authors))
                        .genres(genres).build();

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

}
