package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.book.BookSummaryDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.services.BookService;
import io.spring.training.boot.server.utils.mappers.BookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageProperties storageProperties;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Book> books;

    private final String baseUrl = "/api/v1/books";


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
                .authors(Set.of(a1))
                .genres(Set.of(new Genre("Horror")))
                .build();

        Book b2 = Book.builder()
                .id(2L)
                .title("Book Two")
                .authors(Set.of(a2))
                .genres(Set.of(new Genre("Drama")))
                .build();

        books = List.of(b1, b2);
    }

    @Test
    public void givenExistingBooksAndNoKeyword_whenGetAllBooksIsCalled_thenReturnsBookSummaryDtoPage() throws Exception {
        // arrange
        PageRequest pageable = PageRequest.of(0, 20);
        List<BookSummaryDto> bookSummaryDtos = books.stream().map(b -> BookMapper.toBookSummaryDto(b, 0, 0.0)).toList();
        Page<BookSummaryDto> response = new PageImpl<>(bookSummaryDtos, pageable, bookSummaryDtos.size());

        when(bookService.getAllBooks(any(), isNull())).thenReturn(response);

        // act and assert
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Book One"))
                .andExpect(jsonPath("$.content[1].title").value("Book Two"));

        verify(bookService).getAllBooks(any(), isNull());
    }

    @Test
    public void givenExistingBooksAndKeyword_whenGetAllBooksIsCalled_thenReturnsFilteredBookSummaryDtoPage() throws Exception {
        // arrange
        String keyword = "One";
        PageRequest pageable = PageRequest.of(0, 20);
        List<BookSummaryDto> bookSummaryDtos = books.stream()
                .filter(b -> b.getTitle().contains(keyword))
                .map(b -> BookMapper.toBookSummaryDto(b, 0, 0.0)).toList();
        Page<BookSummaryDto> response = new PageImpl<>(bookSummaryDtos, pageable, bookSummaryDtos.size());

        when(bookService.getAllBooks(any(), eq(keyword))).thenReturn(response);

        // act and assert
        mockMvc.perform(get(baseUrl).param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Book One"));

    }
}
