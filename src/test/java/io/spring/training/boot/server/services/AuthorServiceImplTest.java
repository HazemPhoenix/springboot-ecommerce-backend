package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.author.AuthorRequestDto;
import io.spring.training.boot.server.DTOs.author.AuthorResponseDto;
import io.spring.training.boot.server.DTOs.genre.GenreResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.AuthorNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.repositories.AuthorRepo;
import io.spring.training.boot.server.services.implementations.AuthorServiceImpl;
import io.spring.training.boot.server.services.implementations.FileSystemImageStorageService;
import io.spring.training.boot.server.services.implementations.GenreServiceImpl;
import io.spring.training.boot.server.utils.mappers.AuthorMapper;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceImplTest {
    @Mock
    private AuthorRepo authorRepo;
    @Mock
    private GenreServiceImpl genreService;
    @Mock
    private FileSystemImageStorageService imageStorageService;

    @InjectMocks
    private AuthorServiceImpl authorService;

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
    public void givenExistingAuthors_whenGetAllAuthorsIsCalled_thenReturnsAPageOfAuthorResponseDtos(){

        // Arrange
        Author firstAuthor = authors.get(0);
        Author secondAuthor = authors.get(1);

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

    @Test
    public void givenExistingAuthors_whenGetAuthorByIdIsCalledWithAnExistingId_thenReturnsCorrectAuthor(){
        // Arrange
        when(authorRepo.findById(1L)).thenReturn(Optional.of(authors.get(0)));

        // Act
        AuthorResponseDto authorResponseDto = authorService.getAuthorById(1L);

        // Assert
        verify(authorRepo).findById(1L);
        assertThat(authorResponseDto).isNotNull();
        assertThat(authorResponseDto.name()).isEqualTo(authors.get(0).getName());
    }

    @Test
    public void givenExistingAuthors_whenGetAuthorByIdIsCalledWithANonExistentId_thenThrowsCorrectException(){
        // Arrange
        when(authorRepo.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authorService.getAuthorById(10L)).isExactlyInstanceOf(AuthorNotFoundException.class);

        verify(authorRepo).findById(10L);

    }

    @Test
    public void givenCorrectRequestDtoAndImageFile_whenCreateAuthorIsCalled_thenReturnsCorrectAuthorResponseDto(){
        // Arrange
        String photo = "image.png";
        when(imageStorageService.storeAuthorImage(any(MultipartFile.class))).thenReturn(photo);

        Set<Genre> genres = Set.of(new Genre("Horror"), new Genre("Drama"));
        when(genreService.findGenresByIds(any(Set.class))).thenReturn(genres);

        Author author = Author.builder()
                .id(3L)
                .photo(photo)
                .genres(genres)
                .name("test")
                .bio("test")
                .nationality("test")
                .build();

        when(authorRepo.save(any(Author.class))).thenReturn(author);

        // Act
        AuthorRequestDto authorRequestDto = new AuthorRequestDto("test", "test", "test", Set.of(1L, 2L));
        MultipartFile multipartFile = new MockMultipartFile(photo, new byte[]{1,2,3});
        AuthorResponseDto authorResponseDto = authorService.createAuthor(authorRequestDto, multipartFile);

        // Assert
        verify(imageStorageService).storeAuthorImage(multipartFile);
        verify(genreService).findGenresByIds(Set.of(1L, 2L));
        verify(authorRepo).save(any(Author.class));

        assertThat(authorResponseDto).isNotNull();
        assertThat(authorResponseDto.id()).isEqualTo(3L);
        assertThat(authorResponseDto.name()).isEqualTo("test");
        assertThat(authorResponseDto.bio()).isEqualTo("test");
        assertThat(authorResponseDto.nationality()).isEqualTo("test");
        assertThat(authorResponseDto.genres().stream().map(GenreResponseDto::name).toList()).containsAll(List.of("Horror", "Drama"));
        assertThat(authorResponseDto.photo()).isEqualTo("http://localhost:8080//uploads/image.png");
    }

    @Test
    public void givenIdOfNonExistentAuthor_whenUpdateAuthorIsCalled_thenThrowsCorrectException(){
        // Arrange
        when(authorRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act
        AuthorRequestDto authorRequestDto = new AuthorRequestDto("test", "test", "test", Set.of(1L, 2L));
        MultipartFile multipartFile = new MockMultipartFile("test", new byte[] {1,2,3});

        // Assert
        assertThatThrownBy(() -> authorService.updateAuthor(10L, authorRequestDto, multipartFile)).isExactlyInstanceOf(AuthorNotFoundException.class);
        verify(authorRepo).findById(10L);
    }

    @Test
    public void givenValidIdAndAuthorRequestDto_whenUpdateAuthorIsCalled_thenReturnUpdatedAuthorDto(){
        // Arrange
        when(authorRepo.findById(any(Long.class))).thenReturn(Optional.of(authors.get(0)));
        when(genreService.findGenresByIds(any(Set.class))).thenReturn(authors.get(0).getGenres());
        doNothing().when(imageStorageService).deleteAuthorImage(any(String.class));
        when(imageStorageService.storeAuthorImage(any(MultipartFile.class))).thenReturn("photo.png");

        String newName = "test";
        String newBio = "test";
        String newNat = "test";

        Set<Genre> newGenres = Set.of(new Genre("Horror"), new Genre("Drama"));
        Author author = Author.builder()
                .id(3L)
                .photo("photo.png")
                .genres(newGenres)
                .name(newName)
                .bio(newBio)
                .nationality(newNat).build();

        when(authorRepo.save(any(Author.class))).thenReturn(author);

        Set<Long> genres = Set.of(1L, 2L);
        AuthorRequestDto authorRequestDto = new AuthorRequestDto(newName, newBio, newNat, genres);
        MultipartFile multipartFile = new MockMultipartFile("photo.png", new byte[] {1,2,3});

        // Act
        AuthorResponseDto authorResponseDto = authorService.updateAuthor(1L, authorRequestDto, multipartFile);

        // Assert
        verify(authorRepo).findById(1L);
        verify(genreService).findGenresByIds(genres);
        verify(imageStorageService).deleteAuthorImage("first photo.png");
        verify(imageStorageService).storeAuthorImage(multipartFile);
        verify(authorRepo).save(any(Author.class));

        assertThat(authorResponseDto).isNotNull();
        assertThat(authorResponseDto.id()).isEqualTo(3L);
        assertThat(authorResponseDto.name()).isEqualTo(newName);
        assertThat(authorResponseDto.bio()).isEqualTo(newBio);
        assertThat(authorResponseDto.nationality()).isEqualTo(newNat);
        assertThat(authorResponseDto.genres().stream().map(GenreResponseDto::name).toList()).containsAll(newGenres.stream().map(Genre::getName).toList());
        assertThat(authorResponseDto.photo()).isEqualTo("http://localhost:8080//uploads/photo.png");
    }

}
