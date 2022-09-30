package ru.tokarev.service.productservice;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokarev.entity.Category;
import ru.tokarev.entity.Product;
import ru.tokarev.exception.categoryexception.CategoryNotFoundException;
import ru.tokarev.exception.productexception.ProductBadRequestException;
import ru.tokarev.exception.productexception.ProductExistsException;
import ru.tokarev.exception.productexception.ProductNotFoundException;
import ru.tokarev.repository.CategoryRepository;
import ru.tokarev.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public Product getById(Long id) {

        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product with this id not found")
        );

        product.setCategory((Category) Hibernate.unproxy(product.getCategory()));

        return product;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<Product> getAll(String categoryName) {

        if (categoryName == null) {

            List<Product> productList = Optional.of(productRepository.findAll()).orElseThrow(
                    () -> new ProductNotFoundException("Products not found")
            );

            if (productList.size() == 0) {
                throw new ProductNotFoundException("Products not found");
            }

            for (Product product : productList) {
                product.setCategory((Category) Hibernate.unproxy(product.getCategory()));
            }

            return productList;

        } else {
            Category category = categoryRepository.findByName(categoryName).orElseThrow(
                    () -> new CategoryNotFoundException("Category doesn't exist")
            );

            List<Product> productList = productRepository.findAllByCategory(category).orElseThrow(
                    () -> new ProductNotFoundException("Products not found")
            );

            if (productList.size() == 0) {
                throw new ProductNotFoundException("Products not found");
            }

            for (Product product : productList) {
                product.setCategory((Category) Hibernate.unproxy(product.getCategory()));
            }

            return productList;
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new ProductExistsException("Product with this name already exists");
        } else if (product.getCategory() == null) {
            throw new ProductBadRequestException("Category shouldn't be empty");
        } else if (product.getId() != null) {
            throw new ProductBadRequestException("Id field should be empty");
        } else if (product.getName().isEmpty() || product.getName() == null) {
            throw new ProductBadRequestException("Product name field shouldn't be empty");
        } else if (product.getCategory().getId() == null && product.getCategory().getName() == null) {
            throw new ProductBadRequestException("Category fields shouldn't be empty");
        } else if (product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(
                    () -> new CategoryNotFoundException("Category with this id doesn't exist")
            );
            product.setCategory(category);
            return Optional.of(productRepository.save(product)).orElseThrow(
                    () -> new ProductBadRequestException("Bad request")
            );
        } else {
            Category category = categoryRepository.findByName(product.getCategory().getName()).orElseThrow(
                    () -> new CategoryNotFoundException("Category with this name doesn't exist")
            );
            product.setCategory(category);
            return Optional.of(productRepository.save(product)).orElseThrow(
                    () -> new ProductBadRequestException("Bad request")
            );
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<Product> createProducts(List<Product> productList) {

        List<Product> createdProductList = new ArrayList<>();
        for (Product product : productList) {
            createdProductList.add(createProduct(product));
        }

        return createdProductList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public Product updateProduct(Long id, Product product) {

        Category category;
        Product existingProduct = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product with this id not found"));

        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new ProductExistsException("Product with this name already exists");
        }
        if (product.getName().isEmpty() || product.getName() == null) {
            throw new ProductBadRequestException("Product name field shouldn't be empty");
        } else if (product.getCategory() == null) {
            throw new ProductBadRequestException("Category shouldn't be empty");
        } else if (product.getCategory().getId() == null && product.getCategory().getName() == null) {
            throw new ProductBadRequestException("Category fields shouldn't be empty");
        } else if (product.getCategory().getId() != null) {
            category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(
                    () -> new CategoryNotFoundException("Category with this id doesn't exist")
            );
            existingProduct.setCategory(category);
        } else {
            category = categoryRepository.findByName(product.getCategory().getName()).orElseThrow(
                    () -> new CategoryNotFoundException("Category with this name doesn't exist")
            );
            existingProduct.setCategory(category);
        }

        existingProduct.setName(product.getName());

        return Optional.of(productRepository.save(product)).orElseThrow(
                () -> new ProductBadRequestException("Bad request"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product with this id not found")
        );

        productRepository.deleteById(product.getId());
    }
}
