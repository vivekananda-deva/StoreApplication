package com.padmavathi.store.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.padmavathi.store.entity.Category;
import com.padmavathi.store.entity.Product;
import com.padmavathi.store.exception.ResourceNotFoundException;
import com.padmavathi.store.repository.CategoryRepository;
import com.padmavathi.store.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException(
        		        "Product not found with id: " + id
        		));    
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product createProduct(Product product) {

        if (product.getCategory() == null ||
                product.getCategory().getId() == null) {

            throw new IllegalArgumentException(
                    "Category is required"
            );
        }

        Category category = categoryRepository
                .findById(product.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: "
                                + product.getCategory().getId()
                ));

        product.setCategory(category);

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {

        Product existingProduct = getProductById(id);

        if (updatedProduct.getCategory() == null ||
                updatedProduct.getCategory().getId() == null) {

            throw new IllegalArgumentException(
                    "Category is required"
            );
        }

        Category category = categoryRepository
                .findById(updatedProduct.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: "
                                + updatedProduct.getCategory().getId()
                ));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setSize(updatedProduct.getSize());
        existingProduct.setCategory(category);

        Product savedProduct = productRepository.save(existingProduct);

        return getProductById(savedProduct.getId());
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}