package ru.tokarev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tokarev.entity.Category;
import ru.tokarev.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    Optional<List<Product>> findAllByCategory(Category category);

}
