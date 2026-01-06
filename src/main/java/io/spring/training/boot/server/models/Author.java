package io.spring.training.boot.server.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String bio;
    private String nationality;
    private String photo;
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "author_genres",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    public Author(String name, String bio, String nationality) {
        this.name = name;
        this.bio = bio;
        this.nationality = nationality;
    }
}
