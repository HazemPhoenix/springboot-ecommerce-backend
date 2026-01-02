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
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    @Column(name = "no_of_pages")
    private int numberOfPages;
    private String image;
    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors;
    public Book(String title, String description, BigDecimal price, int numberOfPages) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.numberOfPages = numberOfPages;
        this.image = image;
    }
}
