package io.spring.training.boot.server.controllers;


import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.services.GenreService;
import io.spring.training.boot.server.utils.mappers.GenreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
    public void givenExistingGenres_whenGetAllGenresIsCalled_thenReturnAListOfGenreResponseDtos() throws Exception {
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


}
