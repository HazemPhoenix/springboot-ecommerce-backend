package io.spring.training.boot.server.controllers;


import io.spring.training.boot.server.DTOs.genre.GenreRequestDto;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.GenreNotFoundException;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.security.filters.JwtFilter;
import io.spring.training.boot.server.security.services.JwtService;
import io.spring.training.boot.server.security.services.UserDetailsServiceImpl;
import io.spring.training.boot.server.services.GenreService;
import io.spring.training.boot.server.utils.mappers.GenreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(GenreController.class)
public class GenreControllerTest {
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

    private final String baseUrl = "/api/v1/genres";

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
    public void givenExistingGenres_whenGetAllGenresIsCalled_thenReturnsAListOfGenreResponseDtos() throws Exception {
        // Arrange
        List<GenreResponseDto> response = genres.stream().map(GenreMapper::toGenreResponseDto).toList();
        when(genreService.getAllGenres()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("Horror"))
                .andExpect(jsonPath("$[1].name").value("Drama"));

    }

    @Test
    public void givenValidGenreId_whenGetGenreByIdIsCalled_thenReturnsCorrectGenreResponseDto() throws Exception {
        // Arrange
        Long id = 1L;
        GenreResponseDto response = GenreMapper.toGenreResponseDto(genres.get(0));

        when(genreService.findGenreById(anyLong())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get(baseUrl + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Horror"));
    }

    @Test
    public void givenInvalidId_whenGetGenreByIdIsCalled_thenReturnNotFoundResponse() throws Exception {
        // Arrange
        Long id = 10L;

        when(genreService.findGenreById(anyLong())).thenThrow(GenreNotFoundException.class);

        // Act & Assert
        mockMvc.perform(get(baseUrl + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }
}
