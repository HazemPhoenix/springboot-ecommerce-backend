package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.DTOs.BookSummaryDto;
import io.spring.training.boot.server.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);

    @Query("select b from Book b " +
            "where lower(b.title) like concat('%',lower(:keyword),'%')" +
            "or lower(b.description) like concat('%', lower(:keyword), '%')")
    Page<Book> findAllContainingKeyword(Pageable pageable, String keyword);

    @EntityGraph(attributePaths = {"authors"})
    Optional<Book> findById(Long id);
}
