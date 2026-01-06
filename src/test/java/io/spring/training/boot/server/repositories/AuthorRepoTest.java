package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AuthorRepoTest {
    @Autowired
    private AuthorRepo authorRepo;

    @Test
    public void givenExistingKeyword_whenFindAllContainingKeywordIsCalled_thenReturnsCorrectNumberOfObjects(){
        // arrange
        Author firstAuthor = Author.builder()
                .name("does not containing keyword")
                .bio("test bio")
                .nationality("test nat")
                .photo("test photo")
                .books(Set.of(new Book(), new Book()))
                .genres(Set.of(new Genre(), new Genre())).build();
        List<Author> authorList = new ArrayList<>();
        authorList.add(firstAuthor);
        for(int i = 0; i < 3; i++) {
            String name = "contains author keyword " + i;
            Author author = Author.builder()
                    .name(name)
                    .bio("test bio")
                    .nationality("test nat")
                    .photo("test photo")
                    .books(Set.of(new Book(), new Book()))
                    .genres(Set.of(new Genre(), new Genre())).build();
            authorList.add(author);
        }
        authorRepo.saveAll(authorList);
        // act
        Page<Author> authorPage = authorRepo.findAllContainingKeyword(Pageable.unpaged(), "author");
        // assert
        assertEquals(3, authorPage.getSize());
    }
}

