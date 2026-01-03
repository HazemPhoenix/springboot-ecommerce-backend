package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepo extends JpaRepository<Author, Long> {
    @EntityGraph(attributePaths = "genres")
    Optional<Author> findById(Long id);
}
