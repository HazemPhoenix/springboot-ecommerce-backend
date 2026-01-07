package io.spring.training.boot.server.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private BigDecimal price;
    @Column(name = "no_of_pages")
    private int numberOfPages;
    private int stock;
    private String image;
    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors;

    @ManyToMany
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @OneToMany(mappedBy = "book")
    private Set<Review> reviews;

    public Book(String title, String description, BigDecimal price, int numberOfPages, int stock) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.numberOfPages = numberOfPages;
        this.stock = stock;
        this.image = image;
    }
}
