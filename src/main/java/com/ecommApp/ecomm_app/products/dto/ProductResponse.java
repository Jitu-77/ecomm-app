package com.ecommApp.ecomm_app.products.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Long categoryId,
        String categoryName,
        BigDecimal price,
        Boolean active,
        Integer availableQty,
        Integer reservedQty,
        Long createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
