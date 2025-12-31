package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepo extends JpaRepository<Author, Long> {
}
