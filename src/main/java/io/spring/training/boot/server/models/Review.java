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
    private String content;
    private boolean wasEdited;
}
