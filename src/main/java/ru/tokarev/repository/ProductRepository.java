package ru.tokarev.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.tokarev.entity.Category;
import ru.tokarev.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"category"})
    @NonNull
    Optional<Product> findById(@NonNull Long id);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"category"})
    @NonNull
    List<Product> findAll();

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"category"})
    Optional<Product> findByName(String name);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"category"})
    Optional<List<Product>> findAllByCategory(Category category);

}
