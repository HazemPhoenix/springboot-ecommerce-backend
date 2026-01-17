package io.spring.training.boot.server.models.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ReviewId implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "book_id")
    private Long bookId;
}
