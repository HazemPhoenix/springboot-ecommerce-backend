package io.spring.training.boot.server.controllers.admin;

import io.spring.training.boot.server.DTOs.book.BookCreationResponseDto;
import io.spring.training.boot.server.DTOs.book.BookRequestDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.services.BookService;
import io.spring.training.boot.server.utils.mappers.AuthorMapper;
import io.spring.training.boot.server.utils.mappers.GenreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookController.class)
public class AdminBookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageProperties storageProperties;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Book> books;

    private final String baseUrl = "/api/v1/admin/books";

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

        books = List.of(b1, b2);
    }

    @Test
    public void givenValidBookRequestDtoAndImage_whenCreateBookIsCalled_thenReturnsCorrectBookCreationResponseDto() throws Exception {
        // arrange
        Book b = books.getFirst();
        BookRequestDto request = new BookRequestDto(
                b.getTitle(),
                b.getDescription(),
                b.getPrice(),
                b.getNumberOfPages(),
                b.getStock(),
                Set.of(1L),
                Set.of(1L));

        BookCreationResponseDto response = new BookCreationResponseDto(
                1L,
                b.getTitle(),
                b.getDescription(),
                b.getPrice(),
                b.getNumberOfPages(),
                b.getStock(),
                "book-image.png",
                b.getAuthors().stream().map(AuthorMapper::toAuthorResponseDto).toList(),
                b.getGenres().stream().map(GenreMapper::toGenreResponseDto).toList(),
                List.of()
        );

        MockMultipartFile bookData = new MockMultipartFile(
                "bookData",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile bookImage = new MockMultipartFile(
                "bookImage",
                "book-image.png",
                "image/png",
                "fake-image-content".getBytes()
        );

        when(bookService.createBook(any(BookRequestDto.class), any(MultipartFile.class))).thenReturn(response);

        // act and assert
        mockMvc.perform(multipart(baseUrl)
                        .file(bookData)
                        .file(bookImage))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(response.title()));

        verify(bookService).createBook(any(BookRequestDto.class), any(MultipartFile.class));
    }

    @Test
    public void givenInvalidBookRequestDto_whenCreateBookIsCalled_thenReturnsUnProcessableContentResponse() throws Exception {
        // arrange
        Book b = books.getFirst();
        BookRequestDto request = new BookRequestDto(
                "",
                b.getDescription(),
                b.getPrice(),
                b.getNumberOfPages(),
                b.getStock(),
                Set.of(1L),
                Set.of(1L));

        MockMultipartFile bookData = new MockMultipartFile(
                "bookData",
                "",
                "application/json",
                objectMapper.writeValueAsString(request).getBytes());

        MockMultipartFile bookImage = new MockMultipartFile(
                "bookImage",
                "book-image.png",
                "image/png",
                "fake-image-content".getBytes()
        );

        // act and assert
        mockMvc.perform(multipart(baseUrl)
                        .file(bookData)
                        .file(bookImage))
                .andExpect(status().isUnprocessableContent());

        verify(bookService, never()).createBook(any(BookRequestDto.class), any(MultipartFile.class));
    }

    @Test
    public void givenValidBookIdAndBookRequestDto_whenUpdateBookIsCalled_thenReturnsCorrectBookCreationResponseDto() throws Exception {
        // arrange
        Long bookId = 1L;
        Book b = books.getFirst();
        BookRequestDto request = new BookRequestDto(
                b.getTitle(),
                b.getDescription(),
                b.getPrice(),
                b.getNumberOfPages(),
                b.getStock(),
                Set.of(1L),
                Set.of(1L));

        BookCreationResponseDto response = new BookCreationResponseDto(
                bookId,
                b.getTitle(),
                b.getDescription(),
                b.getPrice(),
                b.getNumberOfPages(),
                b.getStock(),
                "book-image.png",
                b.getAuthors().stream().map(AuthorMapper::toAuthorResponseDto).toList(),
                b.getGenres().stream().map(GenreMapper::toGenreResponseDto).toList(),
                List.of()
        );

        MockMultipartFile bookData = new MockMultipartFile(
                "bookData",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile bookImage = new MockMultipartFile(
                "bookImage",
                "book-image.png",
                "image/png",
                "fake-image-content".getBytes()
        );

        when(bookService.updateBookById(eq(bookId), any(BookRequestDto.class), any(MultipartFile.class))).thenReturn(response);

        // act and assert
        mockMvc.perform(multipart(baseUrl + "/{id}", bookId)
                        .file(bookData)
                        .file(bookImage)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value(response.title()));

        verify(bookService).updateBookById(eq(bookId), any(BookRequestDto.class), any(MultipartFile.class));
    }

    @Test
    public void givenInvalidBookIdAndValidBookRequestDto_whenUpdateBookIsCalled_thenReturnsNotFoundResponse() throws Exception {
        // arrange
        Long bookId = 10L;
        Book b = books.getFirst();
        BookRequestDto request = new BookRequestDto(
                b.getTitle(),
                b.getDescription(),
                b.getPrice(),
                b.getNumberOfPages(),
                b.getStock(),
                Set.of(1L),
                Set.of(1L));

        MockMultipartFile bookData = new MockMultipartFile(
                "bookData",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile bookImage = new MockMultipartFile(
                "bookImage",
                "book-image.png",
                "image/png",
                "fake-image-content".getBytes()
        );

        when(bookService.updateBookById(eq(bookId), any(BookRequestDto.class), any(MultipartFile.class)))
                .thenThrow(BookNotFoundException.class);

        // act and assert
        mockMvc.perform(multipart(baseUrl + "/{id}", bookId)
                        .file(bookData)
                        .file(bookImage)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isNotFound());

        verify(bookService).updateBookById(eq(bookId), any(BookRequestDto.class), any(MultipartFile.class));
    }


    @Test
    public void givenValidBookIdAndInvalidBookRequestDto_whenUpdateBookIsCalled_thenReturnsUnProcessableContentResponse() throws Exception {
        // arrange
        Long bookId = 1L;
        Book b = books.getFirst();
        BookRequestDto request = new BookRequestDto(
                "",
                b.getDescription(),
                b.getPrice(),
                b.getNumberOfPages(),
                b.getStock(),
                Set.of(1L),
                Set.of(1L));

        MockMultipartFile bookData = new MockMultipartFile(
                "bookData",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile bookImage = new MockMultipartFile(
                "bookImage",
                "book-image.png",
                "image/png",
                "fake-image-content".getBytes()
        );

        // act and assert
        mockMvc.perform(multipart(baseUrl + "/{id}", bookId)
                        .file(bookData)
                        .file(bookImage)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isUnprocessableContent());

        verify(bookService, never()).updateBookById(eq(bookId), any(BookRequestDto.class), any(MultipartFile.class));
    }

    @Test
    public void givenBookId_whenDeleteBookIsCalled_thenReturnsNoContentResponse() throws Exception {
        // arrange
        Long bookId = 1L;

        doNothing().when(bookService).deleteBookById(eq(bookId));

        // act and assert
        mockMvc.perform(delete(baseUrl + "/{id}", bookId))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBookById(eq(bookId));
    }
}
