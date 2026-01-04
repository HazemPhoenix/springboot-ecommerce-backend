package io.spring.training.boot.server.models;

import io.spring.training.boot.server.models.embeddables.ReviewId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_reviews")
@Getter
@Setter
@NoArgsConstructor
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
}
