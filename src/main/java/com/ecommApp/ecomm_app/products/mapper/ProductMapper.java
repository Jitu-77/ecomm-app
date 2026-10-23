package com.ecommApp.ecomm_app.products.mapper;

import com.ecommApp.ecomm_app.products.dto.ProductRequest;
import com.ecommApp.ecomm_app.products.dto.ProductResponse;
import com.ecommApp.ecomm_app.products.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductRequest productRequest);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "availableQty", ignore = true)
    @Mapping(target = "reservedQty", ignore = true)
    ProductResponse toResponse(Product product);
}
