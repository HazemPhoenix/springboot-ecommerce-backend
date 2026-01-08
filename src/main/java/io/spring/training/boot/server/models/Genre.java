package io.spring.training.boot.server.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "genres")
    private Set<Book> books;

    @ManyToMany(mappedBy = "genres")
    private Set<Author> authors;

    public Genre(String name) {
        this.name = name;
    }
}
