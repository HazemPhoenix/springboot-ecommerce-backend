package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.genre.GenreRequestDto;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.exceptions.GenreNotFoundException;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.repositories.GenreRepo;
import io.spring.training.boot.server.services.implementations.GenreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceImplTest {
    @Mock
    private GenreRepo genreRepo;

    @InjectMocks
    private GenreServiceImpl genreService;

    private List<Genre> genres;

    @BeforeEach
    public void setup(){
        Genre firstGenre = new Genre("Horror");
        firstGenre.setId(1L);

        Genre secondGenre = new Genre("Drama");
        secondGenre.setId(2L);

        genres = List.of(firstGenre, secondGenre);
    }

    @Test
    public void givenValidId_whenFindGenreByIdIsCalled_thenReturnCorrectGenreResponseDto() {
        // Arrange
        when(genreRepo.findById(1L)).thenReturn(Optional.of(genres.get(0)));

        // Act
        GenreResponseDto response = genreService.findGenreById(1L);

        // Assert
        verify(genreRepo).findById(1L);
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Horror");
    }

    @Test
    public void givenInvalidId_whenFindGenreByIdIsCalled_thenThrowGenreNotFoundException() {
        // Arrange
        when(genreRepo.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> genreService.findGenreById(10L))
                .isInstanceOf(GenreNotFoundException.class);

        verify(genreRepo).findById(10L);
    }

    @Test
    public void whenGetAllGenresIsCalled_thenReturnListOfGenreResponseDtos() {
        // Arrange
        when(genreRepo.findAll()).thenReturn(genres);

        // Act
        List<GenreResponseDto> response = genreService.getAllGenres();

        // Assert
        verify(genreRepo).findAll();
        assertThat(response).hasSize(2);
        assertThat(response.stream().map(GenreResponseDto::name).toList())
                .containsExactlyInAnyOrder("Horror", "Drama");
    }

    @Test
    public void givenValidRequest_whenCreateGenreIsCalled_thenReturnGenreResponseDto() {
        // Arrange
        GenreRequestDto request = new GenreRequestDto("Horror");
        when(genreRepo.save(any(Genre.class))).thenReturn(genres.get(0));

        // Act
        GenreResponseDto response = genreService.createGenre(request);

        // Assert
        verify(genreRepo).save(any(Genre.class));
        assertThat(response.name()).isEqualTo("Horror");
    }

    @Test
    public void givenValidId_whenUpdateGenreIsCalled_thenReturnUpdatedGenreResponseDto() {
        // Arrange
        Long id = 1L;
        GenreRequestDto request = new GenreRequestDto("Thriller");

        when(genreRepo.findById(id)).thenReturn(Optional.of(genres.get(0)));

        when(genreRepo.save(any(Genre.class))).thenAnswer(inv -> {
            Genre g = inv.getArgument(0);
            g.setId(id);
            return g;
        });

        // Act
        GenreResponseDto response = genreService.updateGenre(id, request);

        // Assert
        verify(genreRepo).save(any(Genre.class));
        assertThat(response.name()).isEqualTo("Thriller");
    }

    @Test
    public void givenInvalidId_whenUpdateGenreIsCalled_thenThrowGenreNotFoundException() {
        // Arrange
        Long id = 10L;
        GenreRequestDto request = new GenreRequestDto("Thriller");

        when(genreRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> genreService.updateGenre(id, request))
                .isInstanceOf(GenreNotFoundException.class);

        verify(genreRepo, never()).save(any(Genre.class));
    }

    @Test
    public void givenValidId_whenDeleteGenreByIdIsCalled_thenCallDeleteById() {
        // Arrange
        Long id = 1L;

        // Act
        genreService.deleteGenreById(id);

        // Assert
        verify(genreRepo).deleteById(id);
    }

    @Test
    public void givenSetOfIds_whenFindGenresByIdsIsCalled_thenReturnSetOfGenres() {
        // Arrange
        Set<Long> ids = Set.of(1L, 2L);
        when(genreRepo.findAllById(ids)).thenReturn(genres);

        // Act
        Set<Genre> result = genreService.findGenresByIds(ids);

        // Assert
        verify(genreRepo).findAllById(ids);
        assertThat(result).hasSize(2);
        assertThat(result).containsAll(genres);
    }
}