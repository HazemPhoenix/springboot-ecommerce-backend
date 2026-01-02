package io.spring.training.boot.server.seeder;

import com.github.javafaker.Faker;
import io.spring.training.boot.server.DTOs.AuthorRequestDto;
import io.spring.training.boot.server.DTOs.BookRequestDto;
import io.spring.training.boot.server.repositories.AuthorRepo;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.services.AuthorService;
import io.spring.training.boot.server.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final BookService bookService;
    private final AuthorService authorService;
    private final BookRepo bookRepo;
    private final AuthorRepo authorRepo;

    private final int AUTHOR_COUNT = 100;
    private final int BOOK_COUNT = 50000;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory2;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
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
            AuthorRequestDto authorRequestDto = new AuthorRequestDto(name, bio, nat, photo);
            authorService.createAuthor(authorRequestDto);
        }
    }

    private void seedBooks() throws SQLException {
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
                authorIds.add((long) faker.number().numberBetween(minAuthorId, maxAuthorId));
            }
            // image
            String img = faker.lorem().characters(10);
            BookRequestDto bookRequestDto = new BookRequestDto(title, desc, price, pages, authorIds);
//            bookService.createBook(bookRequestDto, img);
        }
    }
}
