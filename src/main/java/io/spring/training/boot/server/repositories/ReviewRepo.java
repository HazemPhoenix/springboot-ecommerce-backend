package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.Review;
import io.spring.training.boot.server.models.embeddables.ReviewId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepo extends JpaRepository<Review, ReviewId> {
    Optional<Review> findById(ReviewId id);

    Page<Review> findById_BookId(Pageable pageable, Long idBookId);

    @Query("select r from Review r " +
            "where r.id.bookId = :bookId " +
            "and (lower(r.title) like concat('%', lower(:keyword), '%') " +
            "or lower(r.content) like concat('%', lower(:keyword), '%'))")
    Page<Review> findById_BookIdAndKeyword(Pageable pageable, Long bookId, String keyword);
}
