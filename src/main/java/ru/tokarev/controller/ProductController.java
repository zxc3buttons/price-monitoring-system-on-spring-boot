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
import ru.tokarev.dto.CategoryDto;
import ru.tokarev.dto.productdto.CategoryForProductRequestDto;
import ru.tokarev.dto.productdto.ProductDto;
import ru.tokarev.entity.Product;
import ru.tokarev.service.productservice.ProductService;
import ru.tokarev.utils.MapperUtil;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequestMapping("/api/products")
@RestController
public class ProductController {

    private final ProductService productService;

    private final ModelMapper modelMapper;

    @Autowired
    public ProductController(ProductService productService, ModelMapper modelMapper) {
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<List<ProductDto>> getAll(
            @RequestParam(name = "category", required = false) String categoryName) {

        log.info("GET request for /products with data {}", categoryName);

        List<Product> productList = productService.getAll(categoryName);
        List<ProductDto> productDtoList = MapperUtil.convertList(productList, this::convertToProductDto);

        log.info("Response for GET request for /products with data {}", productDtoList);
        for (ProductDto productDto : productDtoList) {
            log.info("id {}, name {}, categoryId {}", productDto.getId(),
                    productDto.getName(), productDto.getCategoryForProductRequestDto().getId());
        }

        return new ResponseEntity<>(productDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {

        log.info("GET request for /products/{} with data {}", id, id);

        Product product = productService.getById(id);
        ProductDto productDto = convertToProductDto(product);

        log.info("Response for GET request for /products/{} with data: id {}, name {}, categoryId {} ", id,
                productDto.getId(), productDto.getName(), productDto.getCategoryForProductRequestDto().getId());

        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {

        log.info("POST request for /products with data: name {}, categoryId {}",
                productDto.getName(), productDto.getId());

        Product product = convertToProductEntity(productDto);
        Product createdProduct = productService.createProduct(product);
        ProductDto createdProductDto = convertToProductDto(createdProduct);

        log.info("Response for POST request for /products with data: name {}, categoryId {}",
                createdProductDto.getName(), createdProductDto.getId());

        return new ResponseEntity<>(createdProductDto, HttpStatus.CREATED);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<List<ProductDto>> createProducts(@Valid @RequestBody List<ProductDto> productDtoList) {

        log.info("POST request for /products/import with data {}", productDtoList);
        for (ProductDto productDto : productDtoList) {
            log.info("name {}, categoryId {}", productDto.getName(),
                    productDto.getCategoryForProductRequestDto().getId());
        }

        List<Product> productList = MapperUtil.convertList(productDtoList, this::convertToProductEntity);
        List<Product> createdProductList = productService.createProducts(productList);
        List<ProductDto> createdProductDtoList = MapperUtil.convertList(createdProductList, this::convertToProductDto);

        log.info("Response for POST request for /products/import with data {}", createdProductDtoList);
        for (ProductDto productDto : createdProductDtoList) {
            log.info("id {}, name {}, categoryId {}",
                    productDto.getId(), productDto.getName(), productDto.getCategoryForProductRequestDto().getId());
        }

        return new ResponseEntity<>(createdProductDtoList, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {

        log.info("PATCH request for /products/{} with data: name {}, categoryId {}",
                id, productDto.getName(), productDto.getCategoryForProductRequestDto().getId());

        Product product = convertToProductEntity(productDto);
        Product updatedProduct = productService.updateProduct(id, product);
        ProductDto updatedProductDto = convertToProductDto(updatedProduct);

        log.info("Response for PATCH request for /products/{} with data: id {}, name {}, categoryId {}",
                id, updatedProductDto.getId(), updatedProductDto.getName(),
                updatedProductDto.getCategoryForProductRequestDto().getId());

        return new ResponseEntity<>(updatedProductDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {

        log.info("DELETE request for /products/{} with data {}", id, id);

        productService.deleteProduct(id);

        log.info("Response for DELETE request for /products/{} with message {}", id, "Product deleted successfully");

        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }

    private ProductDto convertToProductDto(Product product) {
        CategoryForProductRequestDto categoryForProductRequestDto
                = modelMapper.map(product.getCategory(), CategoryForProductRequestDto.class);
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        productDto.setCategoryForProductRequestDto(categoryForProductRequestDto);

        return productDto;
    }

    private Product convertToProductEntity(ProductDto productDto) {
        return modelMapper.map(productDto, Product.class);
    }
}
