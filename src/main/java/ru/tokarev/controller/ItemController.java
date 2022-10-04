package ru.tokarev.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tokarev.dto.ApiErrorDto;
import ru.tokarev.dto.MarketPlaceDto;
import ru.tokarev.dto.item.ItemDto;
import ru.tokarev.dto.item.ProductForItemDto;
import ru.tokarev.dto.item.ProductPriceComparingDto;
import ru.tokarev.dto.item.ProductPriceDifferenceDto;
import ru.tokarev.entity.item.Item;
import ru.tokarev.exception.itemexception.ItemBadRequestException;
import ru.tokarev.service.itemservice.ItemService;
import ru.tokarev.utils.MapperUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/api/items")
@RestController
public class ItemController {

    private final ItemService itemService;

    private final ModelMapper modelMapper;

    @Autowired
    public ItemController(ItemService itemService, ModelMapper modelMapper) {
        this.itemService = itemService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ItemDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<List<ItemDto>> getAll() {

        log.info("GET request for /items with no data");

        List<Item> itemList = itemService.getAll();
        List<ItemDto> itemDtoList = MapperUtil.convertList(itemList, this::convertToItemDto);

        log.info("Response for GET request for /items with data {}", itemDtoList);
        for (Item item : itemList) {
            log.info("serialNumber {}, productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}",
                    item.getId(), item.getProduct().getId(), item.getPrice(), item.getMarketplace().getId(),
                    item.getDateStart(), item.getDateEnd());
        }

        return new ResponseEntity<>(itemDtoList, HttpStatus.OK);
    }

    @GetMapping("/{serial-number}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ItemDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<ItemDto> getById(
            @PathVariable(value = "serial-number") Long serialNumber) {

        log.info("GET request for /items/{} with data {}", serialNumber, serialNumber);

        Item item = itemService.getById(serialNumber);
        ItemDto itemDto = convertToItemDto(item);

        log.info("Response for GET request for /items/{} with itemDto:" +
                        " serialNumber {}, productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}", serialNumber,
                item.getId(), item.getProduct().getId(), item.getPrice(), item.getMarketplace().getId(),
                item.getDateStart(), item.getDateEnd());

        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping("/check-price-dynamic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ItemDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Products, marketplaces or items not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<Object> getItemPriceDynamic(
            @RequestParam(name = "product_id") Long productId,
            @RequestParam(name = "date_start") String dateStart,
            @RequestParam(name = "date_end") String dateEnd,
            @RequestParam(name = "marketplace_id", required = false) Long marketplaceId) {

        log.info("GET request for /check-price-dynamic with params: {}, {}, {}, {}",
                productId, dateStart, dateEnd, marketplaceId);

        LocalDate dateStartConverted = LocalDate.parse(dateStart);
        LocalDate dateEndConverted = LocalDate.parse(dateEnd).plusDays(1);

        if (dateEndConverted.isBefore(dateStartConverted)) {
            throw new ItemBadRequestException("Date end cannot be before date start");
        }

        List<ProductPriceDifferenceDto> productPriceDifferenceDtos = new ArrayList<>();

        if (marketplaceId != null) {
            productPriceDifferenceDtos.add(itemService.checkPriceDynamicForOneItemAndOneMarketplace(
                    productId, dateStartConverted, dateEndConverted, marketplaceId));
        } else {
            productPriceDifferenceDtos = itemService.checkPriceDynamicForOneItem(
                    productId, dateStartConverted, dateEndConverted);
        }

        log.info("Response for GET request for /check-price-dynamic with data: {}", productPriceDifferenceDtos);
        for (ProductPriceDifferenceDto productPriceDifferenceDto : productPriceDifferenceDtos) {
            log.info("productName {}, marketplaceName {}, priceByDayDtoList {}",
                    productPriceDifferenceDto.getProductName(), productPriceDifferenceDto.getMarketplaceName(),
                    productPriceDifferenceDto.getPriceByDayDtoList());
        }

        return new ResponseEntity<>(productPriceDifferenceDtos, HttpStatus.OK);
    }

    @GetMapping("/compare-prices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ItemDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Products or items not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<Object> getItemPriceComparing(
            @RequestParam(name = "product_id", required = false) Long productId,
            @RequestParam(name = "date_start") String dateStart,
            @RequestParam(name = "date_end") String dateEnd) {

        log.info("GET request for /compare-prices with params: {}, {}, {}",
                productId, dateStart, dateEnd);

        LocalDate dateStartConverted = LocalDate.parse(dateStart);
        LocalDate dateEndConverted = LocalDate.parse(dateEnd).plusDays(1);

        List<ProductPriceComparingDto> productPriceComparingDtoList = new ArrayList<>();

        if (productId == null) {
            productPriceComparingDtoList = itemService.getItemsPriceComparing(dateStartConverted, dateEndConverted);

        } else {
            productPriceComparingDtoList.add(itemService.getItemPriceComparing(
                    productId, dateStartConverted, dateEndConverted));
        }

        log.info("Response for GET request for /compare-prices with data: {}", productPriceComparingDtoList);
        for (ProductPriceComparingDto productPriceComparingDto : productPriceComparingDtoList) {
            log.info("productName {} and marketplacePriceMap {}", productPriceComparingDto.getProductName(),
                    productPriceComparingDto.getMarketplaceEverydayPricesMap());
        }

        return new ResponseEntity<>(productPriceComparingDtoList, HttpStatus.OK);
    }
    
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = ItemDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Products or marketplaces not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "Item for this period already added",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto itemDto) {

        log.info("POST request for /items with data:" +
                        " productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}",
                itemDto.getProductForItemDto().getId(), itemDto.getPrice(), itemDto.getMarketPlaceDto().getId(),
                itemDto.getDateStart(), itemDto.getDateEnd());

        if (itemDto.getDateEnd().isBefore((itemDto.getDateStart()))) {
            throw new ItemBadRequestException("Date end cannot be before date start");
        }

        Item item = convertToItemEntity(itemDto);
        Item createdItem = itemService.createItem(item);
        ItemDto createdItemDto = convertToItemDto(createdItem);

        log.info("Response for POST request for /items with data:  " +
                        "serialNumber {}, productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}",
                createdItemDto.getId(),
                createdItemDto.getProductForItemDto().getId(), createdItemDto.getPrice(),
                createdItemDto.getMarketPlaceDto().getId(),
                createdItemDto.getDateStart(), createdItemDto.getDateEnd());

        return new ResponseEntity<>(createdItemDto, HttpStatus.CREATED);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = ItemDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Products or marketplaces not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "Item for this period already added",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<List<ItemDto>> importItems(
            @RequestBody List<ItemDto> itemDtoList) {

        log.info("POST request for /items/import with data {}", itemDtoList);
        for (ItemDto itemDto : itemDtoList) {
            log.info("productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}",
                    itemDto.getProductForItemDto().getId(), itemDto.getPrice(),
                    itemDto.getMarketPlaceDto().getId(), itemDto.getDateStart(), itemDto.getDateEnd());
        }

        for (ItemDto itemDto : itemDtoList) {
            if (itemDto.getDateEnd().isBefore((itemDto.getDateStart()))) {
                throw new ItemBadRequestException("Date end cannot be before date start");
            }
        }

        List<Item> itemList
                = MapperUtil.convertList(itemDtoList, this::convertToItemEntity);
        List<Item> createdProductsOnMarketList =
                itemService.createItems(itemList);
        List<ItemDto> createdProductsOnMarketDtoList =
                MapperUtil.convertList(createdProductsOnMarketList, this::convertToItemDto);

        log.info("Response for POST request for /items/import with data {}", createdProductsOnMarketDtoList);
        for (ItemDto itemDto : createdProductsOnMarketDtoList) {
            log.info("serialNumber {}, productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}",
                    itemDto.getId(), itemDto.getProductForItemDto().getId(), itemDto.getPrice(),
                    itemDto.getMarketPlaceDto().getId(), itemDto.getDateStart(), itemDto.getDateEnd());
        }

        return new ResponseEntity<>(createdProductsOnMarketDtoList, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{serial-number}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ItemDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
    })
    public ResponseEntity<String> deleteItem(@PathVariable(value = "serial-number") Long serialNumber) {

        log.info("DELETE request for /items/{} with data {}", serialNumber, serialNumber);

        itemService.deleteItem(serialNumber);

        log.info("Response for DELETE request for /items/{} with data {}", serialNumber, "Item deleted successfully");

        return new ResponseEntity<>("Item deleted successfully", HttpStatus.OK);
    }

    private ItemDto convertToItemDto(Item item) {
        ProductForItemDto productForItemDto = modelMapper.map(item.getProduct(), ProductForItemDto.class);
        MarketPlaceDto marketPlaceDto = modelMapper.map(item.getMarketplace(), MarketPlaceDto.class);
        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
        itemDto.setProductForItemDto(productForItemDto);
        itemDto.setMarketPlaceDto(marketPlaceDto);

        return itemDto;
    }

    private Item convertToItemEntity(ItemDto itemDto) {
        return modelMapper.map(itemDto, Item.class);
    }
}
