package com.padmavathi.store.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.padmavathi.store.entity.Category;
import com.padmavathi.store.exception.ResourceNotFoundException;
import com.padmavathi.store.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException(
        		        "Category not found with id: " + id
        		));    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);

        existingCategory.setName(updatedCategory.getName());

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}