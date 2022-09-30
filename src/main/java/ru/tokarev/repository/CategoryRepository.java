package ru.tokarev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tokarev.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

}
