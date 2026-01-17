package io.spring.training.boot.server.models;

import io.spring.training.boot.server.models.embeddables.ReviewId;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "user_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @EmbeddedId
    private ReviewId id;
    private int rating;
    private String title;
    private String content;
    private boolean edited;
    @ManyToOne
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private Book book;

    public Review(int rating, String title, String content) {
        this.rating = rating;
        this.title = title;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Review review)) return false;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
