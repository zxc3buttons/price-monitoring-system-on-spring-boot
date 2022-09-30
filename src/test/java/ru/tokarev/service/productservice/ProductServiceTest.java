package ru.tokarev.service.productservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tokarev.entity.Category;
import ru.tokarev.entity.Product;
import ru.tokarev.repository.CategoryRepository;
import ru.tokarev.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void givenProduct_whenGetById_thenReturnProduct() {

        //arrange
        Product existingProduct = new Product(1L, "water", new Category(1L, "drinks"));
        given(productRepository.findById(1L)).willReturn(Optional.of(existingProduct));

        //act
        Product product = productService.getById(1L);

        //assert
        assertEquals(product, existingProduct);
    }

    @Test
    void givenProducts_whenGetAll_thenReturnProducts() {

        //arrange
        Category waterCategory = new Category(1L, "drinks");
        Product existingProduct1 = new Product(1L, "water", waterCategory);
        Product existingProduct2 = new Product(2L, "choco", new Category(2L, "sugar"));

        given(productRepository.findAll()).willReturn(List.of(existingProduct1, existingProduct2));
        given(categoryRepository.findByName(waterCategory.getName())).willReturn(Optional.of(waterCategory));
        given(productRepository.findAllByCategory(waterCategory)).willReturn(Optional.of(List.of(existingProduct1)));

        //act
        List<Product> productList = productService.getAll(null);
        List<Product> productListWithCertainCategory = productService.getAll("drinks");

        //assert
        assertEquals(productList, List.of(existingProduct1, existingProduct2));
        assertEquals(productListWithCertainCategory, List.of(existingProduct1));
    }

    @Test
    void givenCreatedProduct_whenCreateProduct_thenReturnCreatedProduct() {

        //arrange
        Category waterCategory = new Category(1L, "drinks");
        Product productToCreate = new Product(null, "water", waterCategory);
        given(productRepository.save(productToCreate)).willReturn(productToCreate);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(waterCategory));

        //act
        Product product = productService.createProduct(productToCreate);

        //assert
        assertEquals(product, productToCreate);
    }

    @Test
    void givenCreatedProducts_whenCreateProducts_thenReturnListOfCreatedProducts() {

        //arrange
        Category waterCategory = new Category(1L, "drinks");
        Product productToCreate1 = new Product(null, "water", waterCategory);
        Product productToCreate2 = new Product(null, "water", waterCategory);

        given(productRepository.save(productToCreate1)).willReturn(productToCreate1);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(waterCategory));
        given(productRepository.save(productToCreate2)).willReturn(productToCreate2);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(waterCategory));

        //act
        List<Product> productList = productService.createProducts(List.of(productToCreate1, productToCreate2));

        //assert
        assertEquals(productList, List.of(productToCreate1, productToCreate2));
    }

    @Test
    void givenProductToUpdate_whenUpdateProduct_thenReturnUpdatedProduct() {

        //arrange
        Category waterCategory = new Category(1L, "drinks");
        Product existingProduct = new Product(1L, "water", waterCategory);
        Product updProduct = new Product(1L, "daughter", waterCategory);

        given(productRepository.findById(1L)).willReturn(Optional.of(existingProduct));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(waterCategory));
        given(productRepository.save(existingProduct)).willReturn(updProduct);

        //act
        Product product = productService.updateProduct(1L, existingProduct);

        //assert
        assertEquals(product, updProduct);
    }

    @Test
    void givenProduct_whenDeleteProduct_thenNothing() {

        //arrange
        Category waterCategory = new Category(1L, "drinks");
        Product existingProduct = new Product(1L, "water", waterCategory);
        given(productRepository.findById(1L)).willReturn(Optional.of(existingProduct));
        willDoNothing().given(productRepository).deleteById(1L);

        //act
        productService.deleteProduct(1L);

        //assert
        verify(productRepository, times(1)).deleteById(1L);
    }
}