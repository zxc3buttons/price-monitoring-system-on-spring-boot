package ru.tokarev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tokarev.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
