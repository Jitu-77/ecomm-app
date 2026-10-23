package com.ecommApp.ecomm_app.products.services;

import com.ecommApp.ecomm_app.products.dto.ProductRequest;
import com.ecommApp.ecomm_app.products.dto.ProductResponse;
import com.ecommApp.ecomm_app.products.dto.ProductsByCategoryResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);

    List<ProductsByCategoryResponse> getAllProducts();

    ProductResponse getProductById(Long productId);

    ProductResponse updateProduct(Long productId, ProductRequest request);

    void deleteProduct(Long productId);

    ProductResponse updateInventoryQuantity(
            Long productId,
            Integer quantity
    );
}
