package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthorRepo extends JpaRepository<Author, Long> {
    @EntityGraph(attributePaths = "genres")
    Optional<Author> findById(Long id);
    @Query("select a from Author a " +
            "where lower(a.name) like concat('%', lower(:keyword), '%')" +
            "or lower(a.bio) like concat('%', lower(:keyword), '%') " +
            "or lower(a.nationality) like concat('%', lower(:keyword), '%')")
    Page<Author> findAllContainingKeyword(Pageable pageable, String keyword);

}
