package ru.tokarev.service.itemservice;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokarev.dto.item.PriceByDayDto;
import ru.tokarev.dto.item.ProductPriceComparingDto;
import ru.tokarev.dto.item.ProductPriceDifferenceDto;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;
import ru.tokarev.entity.item.Item;
import ru.tokarev.exception.itemexception.ItemBadRequestException;
import ru.tokarev.exception.itemexception.ItemExistsException;
import ru.tokarev.exception.itemexception.ItemNotFoundException;
import ru.tokarev.exception.marketplaceexception.MarketPlaceNotFoundException;
import ru.tokarev.exception.productexception.ProductNotFoundException;
import ru.tokarev.repository.ItemRepository;
import ru.tokarev.repository.MarketplaceRepository;
import ru.tokarev.repository.ProductRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final ProductRepository productRepository;

    private final MarketplaceRepository marketplaceRepository;

    private final Validator validator;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, ProductRepository productRepository,
                           MarketplaceRepository marketplaceRepository, Validator validator) {
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
        this.marketplaceRepository = marketplaceRepository;
        this.validator = validator;
    }


    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public Item getById(Long serialNumber) {
        Item item = itemRepository.findById(serialNumber).orElseThrow(
                () -> new ItemNotFoundException("Item with this id not found")
        );

        item.setProduct((Product) Hibernate.unproxy(item.getProduct()));
        item.setMarketplace((Marketplace) Hibernate.unproxy(item.getMarketplace()));

        return item;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<Item> getAll() {

        List<Item> itemList = Optional.of(itemRepository.findAll()).orElseThrow(
                () -> new ItemNotFoundException("Products on markets not found"));

        if (itemList.size() == 0) {
            throw new ItemNotFoundException("Products on markets not found");
        }

        for (Item item : itemList) {
            item.setProduct((Product) Hibernate.unproxy(item.getProduct()));
            item.setMarketplace((Marketplace) Hibernate.unproxy(item.getMarketplace()));
        }

        return itemList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public ProductPriceDifferenceDto checkPriceDynamicForOneItemAndOneMarketplace(
            Long productId, LocalDate dateStart, LocalDate dateEnd, Long marketplaceId) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product with this id doesn't exist")
        );
        Marketplace marketplace = marketplaceRepository.findById(marketplaceId).orElseThrow(
                () -> new ProductNotFoundException("Marketplace with this id doesn't exist")
        );

        List<Item> itemList = itemRepository.findAllByProductAndMarketplaceAndOrderByDateStartAsc(
                product, marketplace).orElseThrow(
                () -> new ItemNotFoundException("Items not found")
        );

        if (itemList.size() == 0) {
            throw new ItemNotFoundException("Items not found");
        }

        List<LocalDate> datesBetween = dateStart.datesUntil(dateEnd).collect(Collectors.toList());
        List<PriceByDayDto> priceByDayDtoList = createPriceByDayDtoList(itemList, datesBetween);

        return new ProductPriceDifferenceDto(product.getName(), marketplace.getName(), priceByDayDtoList);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<ProductPriceDifferenceDto> checkPriceDynamicForOneItem(
            Long productId, LocalDate dateStart, LocalDate dateEnd) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product with this id doesn't exist")
        );

        List<Marketplace> marketplaceList = Optional.of(marketplaceRepository.findAll()).orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplaces do not exist")
        );

        List<Item> itemList;

        List<LocalDate> datesBetween = dateStart.datesUntil(dateEnd).collect(Collectors.toList());

        List<ProductPriceDifferenceDto> productPriceDifferenceDtoList = new ArrayList<>();
        for (Marketplace marketplace : marketplaceList) {

            itemList = itemRepository.findAllByProductAndMarketplaceAndOrderByDateStartAsc(product, marketplace)
                    .orElseThrow(() -> new ItemNotFoundException("Products on markets not found")
                    );


            List<PriceByDayDto> priceByDayDtoList = createPriceByDayDtoList(itemList, datesBetween);
            productPriceDifferenceDtoList.add(
                    new ProductPriceDifferenceDto(product.getName(), marketplace.getName(), priceByDayDtoList));
        }

        return productPriceDifferenceDtoList;

    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<ProductPriceComparingDto> getItemsPriceComparing(LocalDate dateStart, LocalDate dateEnd) {

        List<Product> productList = Optional.of(productRepository.findAll()).orElseThrow(
                () -> new ProductNotFoundException("Product with this id doesn't exist")
        );

        List<ProductPriceComparingDto> productPriceComparingDtoList = new ArrayList<>();
        for (Product product : productList) {
            ProductPriceComparingDto productPriceComparingDto =
                    getItemPriceComparing(product.getId(), dateStart, dateEnd);
            if(!productPriceComparingDto.getMarketplaceEverydayPricesMap().isEmpty()) {
                productPriceComparingDtoList.add(getItemPriceComparing(product.getId(), dateStart, dateEnd));
            }
        }

        return productPriceComparingDtoList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public ProductPriceComparingDto getItemPriceComparing(
            Long productId, LocalDate dateStart, LocalDate dateEnd) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product with this id not found"));

        List<Item> itemList = itemRepository.findAllByDateStartAfterAndDateEndBeforeAndProductOrderByDateStartAsc(
                dateStart, dateEnd, product).orElseThrow(
                () -> new ItemNotFoundException("No product on market found")
        );

        List<LocalDate> datesBetween = dateStart.datesUntil(dateEnd).collect(Collectors.toList());

        Map<LocalDate, Map<String, Integer>> marketplaceEverydayPricesMap = new TreeMap<>();
        for (Item item : itemList) {
            item.setMarketplace((Marketplace) Hibernate.unproxy(item.getMarketplace()));
            List<LocalDate> datesOfProduct = item.getDateStart().datesUntil(item.getDateEnd())
                    .collect(Collectors.toList());
            for (LocalDate date : datesOfProduct) {
                if (datesBetween.contains(date)) {
                    if (marketplaceEverydayPricesMap.containsKey(date)) {
                        marketplaceEverydayPricesMap.get(date).put(
                                item.getMarketplace().getName(), item.getPrice());
                    } else {
                        Map<String, Integer> marketplacePriceMap = new HashMap<>();
                        marketplacePriceMap.put(item.getMarketplace().getName(), item.getPrice());
                        marketplaceEverydayPricesMap.put(date, marketplacePriceMap);
                    }
                }
            }
        }

        return new ProductPriceComparingDto(product.getName(), marketplaceEverydayPricesMap);

    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public Item createItem(Item item) {

        Set<ConstraintViolation<Item>> violations = validator.validate(item);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Item> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, violations);
        }

        List<Item> productOnMarketListWithCertainProductAndMarketplace =
                itemRepository.findAllByProductAndMarketplaceAndOrderByDateStartAsc(item.getProduct(),
                        item.getMarketplace()).orElseThrow(
                        () -> new ItemNotFoundException("Products on market not found"));

        for (Item certainItem : productOnMarketListWithCertainProductAndMarketplace) {
            if (Objects.equals(certainItem.getProduct().getId(), item.getProduct().getId()) &&
                    !(item.getDateStart().isBefore(certainItem.getDateStart()) &&
                            (item.getDateEnd().isBefore(certainItem.getDateStart()) ||
                                    item.getDateEnd().isEqual(certainItem.getDateStart())) ||
                            (item.getDateStart().isAfter(certainItem.getDateEnd()) ||
                                    item.getDateStart().isEqual(certainItem.getDateEnd())) &&
                                    item.getDateEnd().isAfter(certainItem.getDateEnd()))) {
                throw new ItemExistsException("Product for this period already added");
            }

        }

        Product product = productRepository.findById(item.getProduct().getId()).orElseThrow(
                () -> new ProductNotFoundException("Product with this id doesn't exist")
        );
        Marketplace marketplace = marketplaceRepository.findById(item.getMarketplace().getId()).orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplace with this id not found")
        );

        item.setProduct(product);
        item.setMarketplace(marketplace);
        return Optional.of(itemRepository.save(item)).orElseThrow(
                () -> new ItemBadRequestException("Bad request"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<Item> createItems(List<Item> itemList) {

        List<Item> createdItems = new ArrayList<>();
        for (Item item : itemList) {
            createdItems.add(createItem(item));
        }

        return createdItems;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public void deleteItem(Long serialNumber) {

        Item item = itemRepository.findById(serialNumber).orElseThrow(
                () -> new ItemNotFoundException("Products on market not found"));

        itemRepository.deleteById(item.getId());
    }

    private List<PriceByDayDto> createPriceByDayDtoList(List<Item> itemList,
                                                        List<LocalDate> datesBetween) {
        List<PriceByDayDto> priceByDayDtoList = new ArrayList<>();

        for (Item item : itemList) {

            List<LocalDate> datesOfProduct = item.getDateStart()
                    .datesUntil(item.getDateEnd()).collect(Collectors.toList());

            for (LocalDate date : datesOfProduct) {

                if (datesBetween.contains(date)) {
                    priceByDayDtoList.add(new PriceByDayDto(item.getPrice().toString(), date));
                }
            }

        }

        for (int i = 0; i < priceByDayDtoList.size(); i ++) {

            if(i != priceByDayDtoList.size() - 1 && priceByDayDtoList.get(i).getDate().
                    datesUntil(priceByDayDtoList.get(i + 1).getDate()).count() > 1) {

                List<LocalDate> datesBetweenToItems = priceByDayDtoList.get(i).getDate().plusDays(1).
                        datesUntil(priceByDayDtoList.get(i + 1).getDate()).collect(Collectors.toList());

                for(LocalDate date : datesBetweenToItems) {
                    priceByDayDtoList.add(new PriceByDayDto(priceByDayDtoList.get(i).getPrice(), date));
                }

                priceByDayDtoList.sort(Comparator.comparing(PriceByDayDto::getDate));

            }
        }

        return priceByDayDtoList;
    }
}
