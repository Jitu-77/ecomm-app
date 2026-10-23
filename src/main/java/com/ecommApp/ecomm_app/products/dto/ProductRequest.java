package com.ecommApp.ecomm_app.products.dto;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        String description,
        Long categoryId,
        BigDecimal price,
        Integer initialQty
) {
}
