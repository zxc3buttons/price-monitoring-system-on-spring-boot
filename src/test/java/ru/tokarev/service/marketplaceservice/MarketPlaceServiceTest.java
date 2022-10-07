package ru.tokarev.service.marketplaceservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;
import ru.tokarev.exception.marketplaceexception.MarketPlaceNotFoundException;
import ru.tokarev.exception.productexception.ProductNotFoundException;
import ru.tokarev.repository.MarketplaceRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class MarketPlaceServiceTest {

    @Mock
    private MarketplaceRepository marketplaceRepository;

    @InjectMocks
    private MarketPlaceServiceImpl marketPlaceService;

    @Test
    void getById() {

        //arrange
        Marketplace existingMarketplace = new Marketplace(1L, "Perekrestok");
        given(marketplaceRepository.findById(1L)).willReturn(Optional.of(existingMarketplace));

        //act
        Marketplace marketplace = marketPlaceService.getById(1L);

        //assert
        assertEquals(marketplace, existingMarketplace);
    }

    @Test
    void getAll() {

        //arrange
        Marketplace existingMarketplace1 = new Marketplace(1L, "Perekrestok");
        Marketplace existingMarketplace2 = new Marketplace(2L, "Magnit");
        given(marketplaceRepository.findAll()).willReturn(List.of(existingMarketplace1, existingMarketplace2));

        //act
        List<Marketplace> marketplaceList = marketPlaceService.getAll();

        //assert
        assertEquals(marketplaceList, List.of(existingMarketplace1, existingMarketplace2));

    }

    @Test
    void createMarketPlace() {

        //arrange
        Marketplace marketplaceToCreate = new Marketplace(1L, "Perekrestok");
        given(marketplaceRepository.save(marketplaceToCreate)).willReturn(marketplaceToCreate);

        //act
        Marketplace marketplace = marketPlaceService.createMarketPlace(marketplaceToCreate);

        //assert
        assertEquals(marketplace, marketplaceToCreate);

    }

    @Test
    void updateMarketPlace() {
        //arrange
        Marketplace existingMarketplace = new Marketplace(1L, "Perekrestok");
        Marketplace updatedMarketplace = new Marketplace(1L, "Magnit");
        given(marketplaceRepository.findById(1L)).willReturn(Optional.of(existingMarketplace));
        given(marketplaceRepository.save(existingMarketplace)).willReturn(updatedMarketplace);

        //act
        Marketplace marketplace = marketPlaceService.updateMarketPlace(1L, existingMarketplace);

        //assert
        assertEquals(marketplace, updatedMarketplace);
    }

    @Test
    void deleteMarketPlace() {
        //arrange
        Marketplace existingMarketplace = new Marketplace(1L, "Perekrestok");
        given(marketplaceRepository.findById(1L)).willReturn(Optional.of(existingMarketplace));
        willDoNothing().given(marketplaceRepository).deleteById(1L);

        //act
        marketPlaceService.deleteMarketPlace(1L);

        //assert
        verify(marketplaceRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenNothing_whenFindMarketplace_ThrowNotFoundException() {
        assertThrows(MarketPlaceNotFoundException.class, () -> {
            marketPlaceService.getById(1L);
        });
    }

    @Test
    void givenNothing_whenFindAll_ThrowNotFoundException() {
        assertThrows(MarketPlaceNotFoundException.class, () -> {
            marketPlaceService.getAll();
        });
    }

    @Test
    void givenNothing_whenUpdateMarketplace_ThrowNotFoundException() {
        assertThrows(MarketPlaceNotFoundException.class, () -> {
            marketPlaceService.updateMarketPlace(1L, new Marketplace());
        });
    }

    @Test
    void givenNothing_whenDeleteMarketplace_ThrowNotFoundException() {
        assertThrows(MarketPlaceNotFoundException.class, () -> {
            marketPlaceService.deleteMarketPlace(1L);
        });
    }
}