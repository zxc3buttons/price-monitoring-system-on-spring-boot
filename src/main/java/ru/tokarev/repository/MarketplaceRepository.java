package ru.tokarev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tokarev.entity.Marketplace;

import java.util.Optional;

public interface MarketplaceRepository extends JpaRepository<Marketplace, Long> {

    Optional<Marketplace> findByName(String name);

}
