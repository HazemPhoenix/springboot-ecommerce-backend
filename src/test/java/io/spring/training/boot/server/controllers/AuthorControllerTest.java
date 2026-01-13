package io.spring.training.boot.server.controllers;


import io.spring.training.boot.server.DTOs.author.AuthorRequestDto;
import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.DTOs.error.ErrorResponse;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.AuthorNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.repositories.AuthorRepo;
import io.spring.training.boot.server.services.AuthorService;
import io.spring.training.boot.server.utils.mappers.AuthorMapper;
import io.spring.training.boot.server.utils.mappers.GenreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageProperties storageProperties;

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Author> authors;

    private final String baseUrl = "/api/v1/authors";

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

        authors = List.of(a1, a2);
    }

    @Test
    public void givenExistingAuthorsAndNoKeyword_whenGetAllAuthorsIsCalled_thenReturnsPaginatedAuthorResponseDtos() throws Exception {
        // arrange
        PageRequest pageable = PageRequest.of(0, 20);
        List<AuthorResponseDto> authorResponseDtos = authors.stream().map(AuthorMapper::toAuthorResponseDto).toList();
        Page<AuthorResponseDto> response = new PageImpl<>(authorResponseDtos, pageable, authorResponseDtos.size());
        when(authorService.getAllAuthors(any(Pageable.class), isNull())).thenReturn(response);

        // act and assert
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(authors.size()))
                .andExpect(jsonPath("$.content[0].name").value(authors.get(0).getName()))
                .andExpect(jsonPath("$.content[1].name").value(authors.get(1).getName()));

        verify(authorService).getAllAuthors(any(Pageable.class), isNull());
    }

    @Test
    public void givenExistingAuthorsAndKeyword_whenGetAllAuthorsIsCalled_thenReturnsFilteredPaginatedAuthorResponseDtos() throws Exception {
        // arrange
        String keyword = "first";
        PageRequest pageable = PageRequest.of(0, 20);
        List<AuthorResponseDto> authorResponseDtos = authors.stream()
                .filter(a -> a.getName().toLowerCase().contains(keyword.toLowerCase()))
                .map(AuthorMapper::toAuthorResponseDto)
                .toList();
        Page<AuthorResponseDto> response = new PageImpl<>(authorResponseDtos, pageable, authorResponseDtos.size());
        when(authorService.getAllAuthors(any(Pageable.class), eq(keyword))).thenReturn(response);

        // act and assert
        mockMvc.perform(get(baseUrl).param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value(authors.get(0).getName()));

        verify(authorService).getAllAuthors(any(Pageable.class), eq(keyword));
    }

    @Test
    public void givenValidAuthorId_whenGetAuthorByIdIsCalled_thenReturnsCorrectAuthorResponseDto() throws Exception {
        // arrange
        Long id = 1L;
        AuthorResponseDto response = AuthorMapper.toAuthorResponseDto(authors.get(0));
        when(authorService.getAuthorById(anyLong())).thenReturn(response);

        // act and assert
        mockMvc.perform(get(baseUrl + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(authors.get(0).getName()));

        verify(authorService).getAuthorById(id);
    }

    @Test
    public void givenInValidAuthorId_whenGetAuthorByIdIsCalled_thenReturnsNotFoundResponse() throws Exception {
        // arrange
        Long id = 10L;
        when(authorService.getAuthorById(anyLong())).thenThrow(AuthorNotFoundException.class);

        // act and assert
        mockMvc.perform(get(baseUrl + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));

        verify(authorService).getAuthorById(id);
    }
}

