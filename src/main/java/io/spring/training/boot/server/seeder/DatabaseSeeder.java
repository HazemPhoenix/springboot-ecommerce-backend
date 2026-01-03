package io.spring.training.boot.server.seeder;

import com.github.javafaker.Faker;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.repositories.AuthorRepo;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.repositories.GenreRepo;
import io.spring.training.boot.server.services.AuthorService;
import io.spring.training.boot.server.services.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final AuthorService authorService;
    private final BookRepo bookRepo;
    private final AuthorRepo authorRepo;
    private final GenreRepo genreRepo;

    private final int AUTHOR_COUNT = 100;
    private final int BOOK_COUNT = 50000;
    private final int GENRE_COUNT = 50;
    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;

    @Override
    public void run(String... args) throws Exception {
        if(genreRepo.count() == 0) {
            System.out.println("Running genre seeder");
            seedGenres();
        }
        if(authorRepo.count() == 0) {
            System.out.println("Running author seeder");
            seedAuthors();
        }
        if(bookRepo.count() == 0) {
            System.out.println("Running book seeder");
            seedBooks();
        }
    }

    private void seedAuthors(){
        Faker faker = new Faker();
        for(int i = 0; i < AUTHOR_COUNT; i++) {
            String name = faker.name().name();
            String bio = faker.lorem().fixedString(250);
            String nat = faker.nation().nationality();
            String photo = faker.lorem().fixedString(10);

            Set<Long> genreIds = new HashSet<>();
            int genreCount = faker.number().numberBetween(1, 10);

            int minGenreId = jdbcTemplate.queryForObject("select min(id) from genres", Integer.class);
            int maxGenreId = jdbcTemplate.queryForObject("select max(id) from genres", Integer.class);
            for(int j = 0; j < genreCount; j++) {
                Long genreId = (long) faker.number().numberBetween(minGenreId, maxGenreId + 1);
                genreIds.add(genreId);
            }

            Author author = new Author(name, bio, nat);
            Set<Genre> genres = genreService.findGenresByIds(genreIds);
            author.setGenres(genres);
            authorRepo.save(author);
        }
    }

    private void seedBooks() {
        Faker faker = new Faker();
        for(int i = 0; i < BOOK_COUNT; i++){
            // title
            String title = faker.book().title();
            // description
            String desc = faker.lorem().fixedString(500);
            // price
            BigDecimal price = new BigDecimal(faker.commerce().price());
            // no. of pages
            int pages = faker.number().numberBetween(0, 1000);
            // author ids
            Set<Long> authorIds = new HashSet<>();
            int authorCount = faker.number().numberBetween(1, 10);

            int minAuthorId = jdbcTemplate.queryForObject("select min(id) from authors", Integer.class);
            int maxAuthorId = jdbcTemplate.queryForObject("select max(id) from authors", Integer.class);

            for(int j = 0; j < authorCount; j++){
                authorIds.add((long) faker.number().numberBetween(minAuthorId, maxAuthorId + 1));
            }

            Set<Long> genreIds = new HashSet<>();
            int genreCount = faker.number().numberBetween(1, 10);

            int minGenreId = jdbcTemplate.queryForObject("select min(id) from genres", Integer.class);
            int maxGenreId = jdbcTemplate.queryForObject("select max(id) from genres", Integer.class);
            for(int j = 0; j < genreCount; j++) {
                Long genreId = (long) faker.number().numberBetween(minGenreId, maxGenreId + 1);
                genreIds.add(genreId);
            }

            // image
//            String img = faker.lorem().characters(10);
            Book book = new Book(title, desc, price, pages);
            Set<Author> authors = authorService.findAuthorsByIds(authorIds);
            Set<Genre> genres = genreService.findGenresByIds(genreIds);
            book.setAuthors(authors);
            book.setGenres(genres);
            bookRepo.save(book);
        }
    }

    private void seedGenres(){
        Faker faker = new Faker();
        Set<String> names = new HashSet<>();
        for(int i = 0; i < GENRE_COUNT; i++) {
            String name = faker.book().genre();
            if(names.add(name)){
                Genre genre = new Genre(name);
                genreRepo.save(genre);
            }
        }
    }
}
