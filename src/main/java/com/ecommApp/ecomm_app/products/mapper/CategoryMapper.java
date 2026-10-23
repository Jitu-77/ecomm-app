package com.ecommApp.ecomm_app.products.mapper;

import com.ecommApp.ecomm_app.products.dto.CategoryRequest;
import com.ecommApp.ecomm_app.products.dto.CategoryResponse;
import com.ecommApp.ecomm_app.products.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);
}