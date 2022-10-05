package ru.tokarev.service.marketplaceservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.exception.marketplaceexception.MarketPlaceBadRequestException;
import ru.tokarev.exception.marketplaceexception.MarketPlaceExistsException;
import ru.tokarev.exception.marketplaceexception.MarketPlaceNotFoundException;
import ru.tokarev.repository.MarketplaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MarketPlaceServiceImpl implements MarketPlaceService {

    private final MarketplaceRepository marketplaceRepository;

    @Autowired
    public MarketPlaceServiceImpl(MarketplaceRepository marketplaceRepository) {
        this.marketplaceRepository = marketplaceRepository;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public Marketplace getById(Long id) {
        return marketplaceRepository.findById(id).orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplace with this id not found"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Marketplace> getAll() {

        List<Marketplace> marketplaceList = Optional.of(marketplaceRepository.findAll()).orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplaces not found"));

        if (marketplaceList.size() == 0) {
            throw new MarketPlaceNotFoundException("Marketplaces not found");
        }

        return marketplaceList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Marketplace createMarketPlace(Marketplace marketPlace) {
        if (marketplaceRepository.findByName(marketPlace.getName()).isPresent()) {
            throw new MarketPlaceExistsException("Marketplace with this name already exists");
        }

        return Optional.of(marketplaceRepository.save(marketPlace))
                .orElseThrow(MarketPlaceBadRequestException::new);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public List<Marketplace> createMarketPlaces(List<Marketplace> marketplaceList) {

        List<Marketplace> createdMarketplaceList = new ArrayList<>();
        for (Marketplace marketplace : marketplaceList) {
            createdMarketplaceList.add(createMarketPlace(marketplace));
        }

        return createdMarketplaceList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Marketplace updateMarketPlace(Long id, Marketplace marketPlace) {
        Marketplace existingMarketplace = marketplaceRepository.findById(id).orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplace with this id not found")
        );
        existingMarketplace.setName(marketPlace.getName());

        return Optional.of(marketplaceRepository.save(existingMarketplace))
                .orElseThrow(MarketPlaceBadRequestException::new);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteMarketPlace(Long id) {

        Marketplace marketplace = marketplaceRepository.findById(id).orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplaces not found")
        );

        marketplaceRepository.deleteById(marketplace.getId());
    }
}
