package io.spring.training.boot.server.controllers.admin;


import io.spring.training.boot.server.DTOs.author.AuthorRequestDto;
import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.AuthorNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.services.AuthorService;
import io.spring.training.boot.server.utils.mappers.AuthorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAuthorController.class)
public class AdminAuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageProperties storageProperties;

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Author> authors;

    private final String baseUrl = "/api/v1/admin/authors";


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
    public void givenValidAuthorRequestDto_whenCreateAuthorIsCalled_thenReturnsCorrectAuthorResponseDto() throws Exception {
        // arrange
        Author author = Author.builder()
                .id(3L)
                .name("third author")
                .bio("third bio")
                .nationality("third nat")
                .genres(Set.of(new Genre("Romance"))).build();

        AuthorRequestDto request = new AuthorRequestDto(
                author.getName(),
                author.getBio(),
                author.getNationality(),
                Set.of(3L));

        AuthorResponseDto response = AuthorMapper.toAuthorResponseDto(author);
        MockMultipartFile image = new MockMultipartFile("authorImage", "test.png", MediaType.TEXT_PLAIN_VALUE, "123".getBytes());
        MockMultipartFile authorData = new MockMultipartFile("authorData", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes());

        when(authorService.createAuthor(any(AuthorRequestDto.class), any(MultipartFile.class))).thenReturn(response);

        // act and assert
        mockMvc.perform(multipart(baseUrl)
                        .file(authorData)
                        .file(image))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(response.name()));

        verify(authorService).createAuthor(any(AuthorRequestDto.class), any(MultipartFile.class));
    }

    @Test
    public void givenInValidAuthorRequestDto_whenCreateAuthorIsCalled_thenReturnsCorrectAuthorResponseDto() throws Exception {
        // arrange
        Author author = Author.builder()
                .id(3L)
                .name("third author")
                .bio("third bio")
                .nationality("third nat")
                .genres(Set.of(new Genre("Romance"))).build();

        AuthorRequestDto request = new AuthorRequestDto(
                "",
                author.getBio(),
                author.getNationality(),
                Set.of(3L));

        AuthorResponseDto response = AuthorMapper.toAuthorResponseDto(author);
        MockMultipartFile image = new MockMultipartFile("authorImage", "test.png", MediaType.TEXT_PLAIN_VALUE, "123".getBytes());
        MockMultipartFile authorData = new MockMultipartFile("authorData", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes());

        when(authorService.createAuthor(any(AuthorRequestDto.class), any(MultipartFile.class))).thenReturn(response);

        // act and assert
        mockMvc.perform(multipart(baseUrl)
                        .file(authorData)
                        .file(image))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.UNPROCESSABLE_CONTENT.value()));

        verify(authorService, never()).createAuthor(any(AuthorRequestDto.class), any(MultipartFile.class));
    }

    @Test
    public void givenValidIdAndAuthorRequestDto_whenUpdateAuthorIsCalled_thenReturnsCorrectAuthorResponseDto() throws Exception {
        // arrange
        Long id = 1L;
        Author author = Author.builder()
                .id(id)
                .name("updated author")
                .bio("updated bio")
                .nationality("updated nat")
                .genres(Set.of(new Genre("Sci-Fi"))).build();

        AuthorRequestDto request = new AuthorRequestDto(
                author.getName(),
                author.getBio(),
                author.getNationality(),
                Set.of(4L));

        AuthorResponseDto response = AuthorMapper.toAuthorResponseDto(author);

        when(authorService.updateAuthor(anyLong(), any(AuthorRequestDto.class), any(MultipartFile.class))).thenReturn(response);

        MockMultipartFile image = new MockMultipartFile("authorImage", "test.png", MediaType.TEXT_PLAIN_VALUE, "123".getBytes());
        MockMultipartFile authorData = new MockMultipartFile("authorData", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes());

        // act and assert
        mockMvc.perform(multipart(baseUrl + "/" + id)
                        .file(authorData)
                        .file(image)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(response.name()));

        verify(authorService).updateAuthor(anyLong(), any(AuthorRequestDto.class), any(MultipartFile.class));
    }

    @Test
    public void givenInvalidAuthorIdButValidAuthorRequestDto_whenUpdateAuthorIsCalled_thenReturnsNotFoundResponse() throws Exception {
        // arrange
        Long id = 10L;
        Author author = Author.builder()
                .id(id)
                .name("updated author")
                .bio("updated bio")
                .nationality("updated nat")
                .genres(Set.of(new Genre("Sci-Fi"))).build();

        AuthorRequestDto request = new AuthorRequestDto(
                author.getName(),
                author.getBio(),
                author.getNationality(),
                Set.of(4L));

        when(authorService.updateAuthor(anyLong(), any(AuthorRequestDto.class), any(MultipartFile.class))).thenThrow(AuthorNotFoundException.class);

        MockMultipartFile image = new MockMultipartFile("authorImage", "test.png", MediaType.TEXT_PLAIN_VALUE, "123".getBytes());
        MockMultipartFile authorData = new MockMultipartFile("authorData", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes());

        // act and assert
        mockMvc.perform(multipart(baseUrl + "/" + id)
                        .file(authorData)
                        .file(image)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));

        verify(authorService).updateAuthor(anyLong(), any(AuthorRequestDto.class), any(MultipartFile.class));
    }

    @Test
    public void givenValidAuthorIdButInvalidAuthorRequestDto_whenUpdateAuthorIsCalled_thenReturnsUnprocessableEntityResponse() throws Exception {
        // arrange
        Long id = 1L;
        Author author = Author.builder()
                .id(id)
                .name("updated author")
                .bio("updated bio")
                .nationality("updated nat")
                .genres(Set.of(new Genre("Sci-Fi"))).build();

        AuthorRequestDto request = new AuthorRequestDto(
                "",
                author.getBio(),
                author.getNationality(),
                Set.of(4L));

        AuthorResponseDto response = AuthorMapper.toAuthorResponseDto(author);

        when(authorService.updateAuthor(anyLong(), any(AuthorRequestDto.class), any(MultipartFile.class))).thenReturn(response);

        // act and assert
        MockMultipartFile image = new MockMultipartFile("authorImage", "test.png", MediaType.TEXT_PLAIN_VALUE, "123".getBytes());
        MockMultipartFile authorData = new MockMultipartFile("authorData", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes());

        mockMvc.perform(multipart(baseUrl + "/" + id)
                        .file(authorData)
                        .file(image)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.UNPROCESSABLE_CONTENT.value()));

        verify(authorService, never()).updateAuthor(anyLong(), any(AuthorRequestDto.class), any(MultipartFile.class));
    }

    @Test
    public void givenAuthorId_whenDeleteAuthorIsCalled_thenReturnsNoContentResponse() throws Exception {
        // arrange
        Long id = 1L;
        doNothing().when(authorService).deleteAuthorById(anyLong());

        // act and assert
        mockMvc.perform(delete(baseUrl + "/" + id))
                .andExpect(status().isNoContent());

        verify(authorService).deleteAuthorById(id);
    }
}
