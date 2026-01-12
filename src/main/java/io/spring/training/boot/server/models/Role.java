package io.spring.training.boot.server.models;

import io.spring.training.boot.server.models.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "users")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RoleType name;
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(RoleType name) {
        this.name = name;
    }
}
