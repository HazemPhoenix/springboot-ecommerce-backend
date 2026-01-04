package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.Role;
import io.spring.training.boot.server.models.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
