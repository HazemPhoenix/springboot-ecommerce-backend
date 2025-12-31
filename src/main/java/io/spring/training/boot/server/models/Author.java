package io.spring.training.boot.server.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String bio;
    private String nationality;
    private String photo;

    public Author(String name, String bio, String nationality, String photo) {
        this.name = name;
        this.bio = bio;
        this.nationality = nationality;
        this.photo = photo;
    }
}
