package ru.tokarev.service.itemservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tokarev.dto.item.PriceByDayDto;
import ru.tokarev.dto.item.ProductPriceComparingDto;
import ru.tokarev.dto.item.ProductPriceDifferenceDto;
import ru.tokarev.entity.Category;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;
import ru.tokarev.entity.item.Item;
import ru.tokarev.exception.categoryexception.CategoryNotFoundException;
import ru.tokarev.exception.itemexception.ItemNotFoundException;
import ru.tokarev.exception.marketplaceexception.MarketPlaceNotFoundException;
import ru.tokarev.exception.productexception.ProductNotFoundException;
import ru.tokarev.exception.userexception.UserNotFoundException;
import ru.tokarev.repository.ItemRepository;
import ru.tokarev.repository.MarketplaceRepository;
import ru.tokarev.repository.ProductRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MarketplaceRepository marketplaceRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void givenItem_whenGetBySerialNumber_thenReturnItem() {
        //arrange
        Category category = new Category(1L, "drinks");
        Product product = new Product(1L, "milk", category);
        Marketplace marketplace = new Marketplace(1L, "Magnit");
        Item existingItem = new Item(1L, 100, LocalDate.now(),
                LocalDate.now().plusDays(1), product, marketplace);

        given(itemRepository.findById(1L)).willReturn(Optional.of(existingItem));

        //act
        Item item = itemService.getById(1L);

        //assert
        assertEquals(item, existingItem);
    }

    @Test
    void givenItems_whenGetAll_thenReturnItems() {

        //arrange
        Category category = new Category(1L, "drinks");
        Product product1 = new Product(1L, "milk", category);
        Marketplace marketplace = new Marketplace(1L, "Magnit");
        Item existingItem1 = new Item(1L, 100, LocalDate.now(),
                LocalDate.now().plusDays(1), product1, marketplace);
        Product product2 = new Product(2L, "water", category);
        Item existingItem2 = new Item(1L, 80, LocalDate.now(),
                LocalDate.now().plusDays(1), product2, marketplace);

        given(itemRepository.findAll()).willReturn(List.of(existingItem1, existingItem2));

        //act
        List<Item> itemList = itemService.getAll();

        //assert
        assertEquals(itemList, List.of(existingItem1, existingItem2));

    }

    @Test
    void givenItem_whenGetPriceChangingForOneProductAndOneMarketplace_thanReturnProductPriceDifferenceDto() {

        //arrange
        Category category = new Category(1L, "drinks");
        Product product = new Product(1L, "milk", category);
        Marketplace marketplace = new Marketplace(1L, "Magnit");

        LocalDate dateStart = LocalDate.now().minusDays(3);
        LocalDate dateFirst = LocalDate.now().minusDays(2);
        LocalDate dateSecond = LocalDate.now().minusDays(1);
        LocalDate dateThird = LocalDate.now();
        LocalDate dateEnd = LocalDate.now().plusDays(1);

        Item existingItem1 = new Item(1L, 100,
                dateStart, dateThird, product, marketplace);
        Item existingItem2 = new Item(2L, 80,
                dateThird, dateEnd, product, marketplace);

        List<PriceByDayDto> priceByDayDtoList = List.of(
                new PriceByDayDto("100", dateStart),
                new PriceByDayDto("100", dateFirst),
                new PriceByDayDto("100", dateSecond),
                new PriceByDayDto("80", dateThird));

        ProductPriceDifferenceDto createdProductPriceDifferenceDto = new ProductPriceDifferenceDto(product.getName(),
                marketplace.getName(), priceByDayDtoList);

        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(marketplaceRepository.findById(1L)).willReturn(Optional.of(marketplace));
        given(itemRepository.findAllByProductAndMarketplaceAndOrderByDateStartAsc(product, marketplace)).willReturn(
                Optional.of(List.of(existingItem1, existingItem2)));

        //act
        ProductPriceDifferenceDto productPriceDifferenceDto =
                itemService.checkPriceDynamicForOneItemAndOneMarketplace(
                        product.getId(), dateStart, dateEnd, marketplace.getId());

        //assert
        assertThat(productPriceDifferenceDto).usingRecursiveComparison().isEqualTo(createdProductPriceDifferenceDto);

    }

    @Test
    void givenItem_whenGetPriceChangingForOneProduct_thenReturnProductPriceDifferenceDtos() {

        //arrange
        Category category = new Category(1L, "drinks");
        Product product = new Product(1L, "milk", category);
        Marketplace marketplace1 = new Marketplace(1L, "Magnit");
        Marketplace marketplace2 = new Marketplace(2L, "Perekrestok");

        LocalDate dateStart = LocalDate.now().minusDays(3);
        LocalDate dateFirst = LocalDate.now().minusDays(2);
        LocalDate dateSecond = LocalDate.now().minusDays(1);
        LocalDate dateThird = LocalDate.now();
        LocalDate dateEnd = LocalDate.now().plusDays(1);

        Item existingItem1 = new Item(1L, 100,
                dateStart, dateThird, product, marketplace1);
        Item existingItem2 = new Item(2L, 80,
                dateSecond, dateEnd, product, marketplace2);

        List<PriceByDayDto> priceByDayDtoList1 = List.of(
                new PriceByDayDto("100", dateStart),
                new PriceByDayDto("100", dateFirst),
                new PriceByDayDto("100", dateSecond));

        List<PriceByDayDto> priceByDayDtoList2 = List.of(
                new PriceByDayDto("80", dateSecond),
                new PriceByDayDto("80", dateThird));


        ProductPriceDifferenceDto createdProductPriceDifferenceDto1 = new ProductPriceDifferenceDto(product.getName(),
                marketplace1.getName(), priceByDayDtoList1);

        ProductPriceDifferenceDto createdProductPriceDifferenceDto2 = new ProductPriceDifferenceDto(product.getName(),
                marketplace2.getName(), priceByDayDtoList2);

        List<ProductPriceDifferenceDto> createdProductPriceDifferenceDtoList = List.of(
                createdProductPriceDifferenceDto1, createdProductPriceDifferenceDto2
        );

        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(marketplaceRepository.findAll()).willReturn(List.of(marketplace1, marketplace2));

        given(itemRepository.findAllByProductAndMarketplaceAndOrderByDateStartAsc(product, marketplace1)).willReturn(
                Optional.of(List.of(existingItem1)));
        given(itemRepository.findAllByProductAndMarketplaceAndOrderByDateStartAsc(product, marketplace2)).willReturn(
                Optional.of(List.of(existingItem2)));

        //act
        List<ProductPriceDifferenceDto> productPriceDifferenceDtoList =
                itemService.checkPriceDynamicForOneItem(
                        product.getId(), dateStart, dateEnd);

        //assert
        assertThat(productPriceDifferenceDtoList).usingRecursiveComparison()
                .isEqualTo(createdProductPriceDifferenceDtoList);

    }

    @Test
    void givenItem_whenGetProductPriceComparing_thenReturnProductPriceComparingDto() {

        //arrange
        Category category = new Category(1L, "drinks");
        Product product = new Product(1L, "milk", category);

        Marketplace marketplace1 = new Marketplace(1L, "Magnit");
        Marketplace marketplace2 = new Marketplace(2L, "Perekrestok");

        LocalDate dateStart = LocalDate.now().minusDays(3);
        LocalDate dateFirst = LocalDate.now().minusDays(2);
        LocalDate dateSecond = LocalDate.now().minusDays(1);
        LocalDate dateThird = LocalDate.now();
        LocalDate dateEnd = LocalDate.now().plusDays(1);

        Item existingItem1 = new Item(1L, 100,
                dateStart, dateThird, product, marketplace1);
        Item existingItem2 = new Item(2L, 80,
                dateSecond, dateEnd, product, marketplace2);

        Map<LocalDate, Map<String, Integer>> marketplaceEverydayPricesMap = new HashMap<>();
        marketplaceEverydayPricesMap.put(dateStart, Map.of("Magnit", 100));
        marketplaceEverydayPricesMap.put(dateFirst, Map.of("Magnit", 100));
        marketplaceEverydayPricesMap.put(dateSecond, Map.of("Magnit", 100, "Perekrestok", 80));
        marketplaceEverydayPricesMap.put(dateThird, Map.of("Perekrestok", 80));

        ProductPriceComparingDto createdProductPriceComparingDto = new ProductPriceComparingDto(product.getName(),
                marketplaceEverydayPricesMap);

        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(itemRepository.findAllByDateStartAfterAndDateEndBeforeAndProductOrderByDateStartAsc(
                dateStart, dateEnd, product)).willReturn(
                Optional.of(List.of(existingItem1, existingItem2))
        );

        //act
        ProductPriceComparingDto productPriceComparingDto = itemService.getItemPriceComparing(
                product.getId(), dateStart, dateEnd
        );

        //assert
        assertThat(productPriceComparingDto).usingRecursiveComparison()
                .isEqualTo(createdProductPriceComparingDto);

    }

    @Test
    void givenProductAndDatesAndMarketplaceAndPrice_whenCreateItem_thenReturnCreatedItem() {

        //arrange
        Category category = new Category(1L, "drinks");
        Product product = new Product(1L, "milk", category);

        Marketplace marketplace = new Marketplace(1L, "Magnit");

        LocalDate dateStart1 = LocalDate.now().minusDays(3);
        LocalDate dateEnd1 = LocalDate.now().minusDays(1);
        LocalDate dateStart2 = LocalDate.now().plusDays(1);
        LocalDate dateEnd2 = LocalDate.now().plusDays(3);

        Item item1 = new Item(null, 100, dateStart1, dateEnd1, product, marketplace);
        Item item2 = new Item(1L, 100, dateStart2, dateEnd2, product, marketplace);

        given(itemRepository.findAllByProductAndMarketplaceAndOrderByDateStartAsc(product, marketplace))
                .willReturn(Optional.of(List.of(item2)));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(marketplaceRepository.findById(marketplace.getId())).willReturn(Optional.of(marketplace));
        given(itemRepository.save(item1)).willReturn(item1);

        //act
        Item actualItem = itemService.createItem(item1);

        //assert
        assertThat(actualItem).usingRecursiveComparison().isEqualTo(item1);
    }

    @Test
    void givenListOfProductsAndDatesAndMarketplaceAndPrice_whenCreateItems_thenReturnListOfCreatedItems() {

        //arrange
        Category category = new Category(1L, "drinks");
        Product product = new Product(1L, "milk", category);

        Marketplace marketplace = new Marketplace(1L, "Magnit");

        LocalDate dateStart1 = LocalDate.now().minusDays(3);
        LocalDate dateEnd1 = LocalDate.now().minusDays(1);
        LocalDate dateStart2 = LocalDate.now().plusDays(1);
        LocalDate dateEnd2 = LocalDate.now().plusDays(3);
        LocalDate dateStart3 = LocalDate.now().plusDays(5);
        LocalDate dateEnd3 = LocalDate.now().plusDays(8);

        Item item1 = new Item(1L, 100, dateStart1, dateEnd1, product, marketplace);
        Item item2 = new Item(2L, 100, dateStart2, dateEnd2, product, marketplace);
        Item item3 = new Item(3L, 100, dateStart3, dateEnd3, product, marketplace);

        given(itemRepository.findAllByProductAndMarketplaceAndOrderByDateStartAsc(product, marketplace)).
                willReturn(Optional.of(List.of(item3)));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(marketplaceRepository.findById(marketplace.getId())).willReturn(Optional.of(marketplace));
        given(itemRepository.save(item1)).willReturn(item1);

        given(itemRepository.findAllByProductAndMarketplaceAndOrderByDateStartAsc(product, marketplace)).
                willReturn(Optional.of(List.of(item3)));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(marketplaceRepository.findById(marketplace.getId())).willReturn(Optional.of(marketplace));
        given(itemRepository.save(item2)).willReturn(item2);

        //act
        List<Item> actualItems = itemService.createItems(List.of(item1, item2));

        //assert
        assertThat(actualItems).usingRecursiveComparison().isEqualTo(List.of(item1, item2));
    }

    @Test
    void givenItem_whenDeleteItem_thenNothing() {

        //arrange
        Category category = new Category(1L, "drinks");
        Product product = new Product(1L, "milk", category);

        Marketplace marketplace = new Marketplace(1L, "Magnit");

        LocalDate dateStart = LocalDate.now().minusDays(3);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        Item existingItem = new Item(1L, 100, dateStart, dateEnd, product, marketplace);
        given(itemRepository.findById(1L)).willReturn(Optional.of(existingItem));
        willDoNothing().given(itemRepository).deleteById(1L);

        //act
        itemService.deleteItem(1L);

        //assert
        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenNothing_whenFindItem_ThrowNotFoundException() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.getById(1L);
        });
    }

    @Test
    void givenNothing_whenFindAll_ThrowNotFoundException() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.getAll();
        });
    }

    @Test
    void givenNothing_whenDeleteItem_ThrowNotFoundException() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.deleteItem(1L);
        });
    }

    @Test
    void givenNothing_whenCheckPriceDynamicForOneItemAndOneMarketplace_ThrowNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> {
            itemService.checkPriceDynamicForOneItemAndOneMarketplace(
                    null, null, null, null);
        });
    }

    @Test
    void givenNothing_whenCheckPriceDynamicForOneItem_ThrowNotFoundException() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.checkPriceDynamicForOneItem(null,  null, null);
        });
    }

    @Test
    void givenNothing_whenGetItemPriceComparing_ThrowNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> {
            itemService.getItemPriceComparing(null, null, null);
        });
    }
}