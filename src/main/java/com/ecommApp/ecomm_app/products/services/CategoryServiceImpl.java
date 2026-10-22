package com.ecommApp.ecomm_app.products.services;

import com.ecommApp.ecomm_app.common.exceptions.DuplicateResourceFoundException;
import com.ecommApp.ecomm_app.common.exceptions.ResourceNotFoundException;
import com.ecommApp.ecomm_app.products.dto.CategoryRequest;
import com.ecommApp.ecomm_app.products.dto.CategoryResponse;
import com.ecommApp.ecomm_app.products.entity.Category;
import com.ecommApp.ecomm_app.products.mapper.CategoryMapper;
import com.ecommApp.ecomm_app.products.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceFoundException(
                    "Category already exists with name: " + request.name()
            );
        }

        Category category = categoryMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();
        category.setCreatedAt(now);
        category.setUpdatedAt(now);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> categoryMapper.toResponse(category))
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        if (!category.getName().equals(request.name())
                && categoryRepository.existsByName(request.name())) {

            throw new DuplicateResourceFoundException(
                    "Category already exists with name: " + request.name()
            );
        }

        category.setName(request.name());
        category.setDescription(request.description());

        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        categoryRepository.delete(category);
    }
}