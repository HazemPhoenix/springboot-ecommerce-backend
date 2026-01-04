package io.spring.training.boot.server.models;

import io.spring.training.boot.server.models.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private RoleType name;
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(RoleType name) {
        this.name = name;
    }
}
