package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.projections.BookWithStats;
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

    @Query("select b as book, coalesce(count(r.rating), 0) as totalReviews,  round(coalesce(avg(r.rating), 0.0), 2) as averageRating " +
            "from Book b " +
            "left join Review r " +
            "on b.id = r.id.bookId " +
            "group by b ")
    Page<BookWithStats> findAllBooksWithStats(Pageable pageable);

    @Query("select b as book, coalesce(count(r.rating),0) as totalReviews, round(coalesce(avg(r.rating),0.0), 2) as averageRating " +
            "from Book b " +
            "left join Review r " +
            "on b.id = r.id.bookId " +
            "where lower(b.title) like concat('%',lower(:keyword),'%') " +
            "or lower(b.description) like concat('%', lower(:keyword), '%')" +
            "group by b ")
    Page<BookWithStats> findAllBooksWithStatsContaining(Pageable pageable, String keyword);

    @EntityGraph(attributePaths = {"authors"})
    Optional<Book> findById(Long id);
}
