package ru.tokarev.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;
import ru.tokarev.entity.item.Item;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"product", "marketplace"})
    @NonNull
    Optional<Item> findById(@NonNull Long id);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"product", "marketplace"})
    @NonNull
    List<Item> findAll();

    @Query("SELECT i FROM Item i WHERE i.product = :product and i.marketplace = :marketplace order by i.dateStart asc")
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"product", "marketplace"})
    Optional<List<Item>> findAllByProductAndMarketplaceAndOrderByDateStartAsc(
            @Param("product") Product product, @Param("marketplace") Marketplace marketplace);

    @Query("SELECT i FROM Item i WHERE i.product = :product order by i.dateStart asc")
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"product", "marketplace"})
    Optional<List<Item>> findAllByProductAndOrderByDateStartAsc(@Param("product") Product product);

    @Query("SELECT i FROM Item i WHERE i.product = :product" +
            " and i.dateStart >= :dateStart and i.dateEnd <= :dateEnd order by i.dateStart asc")
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"product", "marketplace"})
    Optional<List<Item>> findAllByDateStartAfterAndDateEndBeforeAndProductOrderByDateStartAsc(
            @Param("dateStart") LocalDate dateStart, @Param("dateEnd") LocalDate dateEnd,
            @Param("product") Product product);

    @Query("SELECT i FROM Item i WHERE i.product = :product and i.marketplace = :marketplace" +
            " and i.dateStart >= :dateStart and i.dateEnd <= :dateEnd order by i.dateStart asc")
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"product", "marketplace"})
    Optional<List<Item>> findProductsByDateAndProductAndMarketplaceAndOrderByDateStartAsc(
            @Param("product") Product product, @Param("dateStart") LocalDate dateStart,
            @Param("dateEnd") LocalDate dateEnd, @Param("marketplace") Marketplace marketplace);

}
