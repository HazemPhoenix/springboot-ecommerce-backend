package io.spring.training.boot.server.controllers.admin;

import io.spring.training.boot.server.DTOs.genre.GenreRequestDto;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.GenreNotFoundException;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.security.filters.JwtFilter;
import io.spring.training.boot.server.security.services.JwtService;
import io.spring.training.boot.server.security.services.UserDetailsServiceImpl;
import io.spring.training.boot.server.services.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminGenreController.class)
public class AdminGenreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private StorageProperties storageProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private List<Genre> genres;

    private final String baseUrl = "/api/v1/admin/genres";

    @BeforeEach
    public void setup(){
        Genre g1 = Genre.builder()
                .id(1L)
                .name("Horror").build();

        Genre g2 = Genre.builder()
                .id(2L)
                .name("Drama").build();

        genres = List.of(g1, g2);
    }

    @Test
    public void givenValidGenreRequestDto_whenCreateGenreIsCalled_thenReturnsCorrectGenreResponseDto() throws Exception {
        // Arrange
        GenreRequestDto request = new GenreRequestDto("Romance");
        GenreResponseDto response = new GenreResponseDto(3L, "Romance");
        when(genreService.createGenre(any(GenreRequestDto.class))).thenReturn(response);


        // Act & Assert
        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Romance"));
    }

    @Test
    public void givenInvalidGenreRequestDto_whenCreateGenreIsCalled_thenReturnsUnprocessableContentResponse() throws Exception {
        // Arrange
        GenreRequestDto request = new GenreRequestDto("");

        // Act & Assert
        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNPROCESSABLE_CONTENT.value()));

        verify(genreService, never()).createGenre(any());
    }

    @Test
    public void givenValidIdAndGenreRequestDto_whenUpdateGenreIsCalled_thenReturnsCorrectGenreResponseDto() throws Exception {
        // Arrange
        Long id = 1L;
        GenreRequestDto request = new GenreRequestDto("Romance");
        GenreResponseDto response = new GenreResponseDto(1L, "Romance");

        when(genreService.updateGenre(id, request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put(baseUrl + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(response.name()));
    }

    @Test
    public void givenInvalidIdButCorrectGenreRequestDto_whenUpdateGenreIsCalled_thenReturnsNotFoundResponse() throws Exception {
        // Arrange
        Long id = 10L;
        GenreRequestDto request = new GenreRequestDto("Romance");

        when(genreService.updateGenre(id, request)).thenThrow(GenreNotFoundException.class);

        // Act & Assert
        mockMvc.perform(put(baseUrl + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void givenValidIdButInvalidGenreRequestDto_whenUpdateGenreIsCalled_thenReturnsUnprocessableContentResponse() throws Exception {
        // Arrange
        Long id = 1L;
        GenreRequestDto request = new GenreRequestDto("");

        when(genreService.updateGenre(anyLong(), any(GenreRequestDto.class))).thenReturn(new GenreResponseDto(id, ""));

        // Act and Assert
        mockMvc.perform(put(baseUrl + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNPROCESSABLE_CONTENT.value()));

        verify(genreService, never()).updateGenre(anyLong(), any());
    }

    @Test
    public void givenGenreId_whenDeleteGenreIsCalled_thenReturnsNoContentResponse() throws Exception {
        // Arrange
        Long id = 1L;

        doNothing().when(genreService).deleteGenreById(id);

        // Act and Assert
        mockMvc.perform(delete(baseUrl + "/" + id))
                .andExpect(status().isNoContent());

        verify(genreService).deleteGenreById(id);
    }
}
