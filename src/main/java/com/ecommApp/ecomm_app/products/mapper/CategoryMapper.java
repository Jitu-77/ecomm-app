package com.ecommApp.ecomm_app.products.mapper;

import com.ecommApp.ecomm_app.products.dto.CategoryRequest;
import com.ecommApp.ecomm_app.products.dto.CategoryResponse;
import com.ecommApp.ecomm_app.products.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);
}