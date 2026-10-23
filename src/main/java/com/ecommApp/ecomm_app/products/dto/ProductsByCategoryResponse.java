package com.ecommApp.ecomm_app.products.dto;

import java.util.List;

public record ProductsByCategoryResponse(
        Long categoryId,
        String categoryName,
        List<ProductResponse> products
) {
}
