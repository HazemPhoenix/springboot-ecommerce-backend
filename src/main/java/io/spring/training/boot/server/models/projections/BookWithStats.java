package io.spring.training.boot.server.models.projections;

import io.spring.training.boot.server.models.Book;

public interface BookWithStats {
    Book getBook();
    int getTotalReviews();
    double getAverageRating();
}
