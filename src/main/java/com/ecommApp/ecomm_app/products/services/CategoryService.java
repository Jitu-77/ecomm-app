package com.ecommApp.ecomm_app.products.services;

import com.ecommApp.ecomm_app.products.dto.CategoryRequest;
import com.ecommApp.ecomm_app.products.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}