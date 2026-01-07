package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.repositories.AuthorRepo;
import io.spring.training.boot.server.services.implementations.AuthorServiceImpl;
import io.spring.training.boot.server.services.implementations.GenreServiceImpl;
import io.spring.training.boot.server.utils.mappers.AuthorMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceImplTest {
    @Mock
    private AuthorRepo authorRepo;
    @Mock
    private GenreServiceImpl genreService;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    public void givenExistingAuthors_whenGetAllAuthorsIsCalled_thenReturnsAPageOfAuthorResponseDtos(){

        // Arrange
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

        Page<Author> authorPage = new PageImpl<>(List.of(firstAuthor, secondAuthor));

        when(authorRepo.findAll(any(Pageable.class))).thenReturn(authorPage);
        when(authorRepo.findAllContainingKeyword(any(Pageable.class), eq("first"))).thenReturn(new PageImpl<>(List.of(firstAuthor)));

        // Act
        Page<AuthorResponseDto> authorResponseDtos1 = authorService.getAllAuthors(PageRequest.of(1, 10), "");
        Page<AuthorResponseDto> authorResponseDtos2 = authorService.getAllAuthors(PageRequest.of(1, 10), "first");

        // Assert
        verify(authorRepo).findAll(any(Pageable.class));
        verify(authorRepo).findAllContainingKeyword(any(Pageable.class), eq("first"));

        assertThat(authorResponseDtos1.getSize()).isEqualTo(2);
        assertThat(authorResponseDtos1.map(AuthorResponseDto::name).toList()).containsAll(List.of(firstAuthor.getName(), secondAuthor.getName()));
        assertThat(authorResponseDtos1.map(AuthorResponseDto::photo).toList()).allMatch(image -> image.contains("http://localhost:8080//uploads/"));
        assertThat(authorResponseDtos2.getSize()).isEqualTo(1);
        assertThat(authorResponseDtos2.getContent().getFirst().name()).isEqualTo(firstAuthor.getName());
        assertThat(authorResponseDtos2.getContent().getFirst().photo()).isEqualTo("http://localhost:8080//uploads/" + firstAuthor.getPhoto());
    }

}
