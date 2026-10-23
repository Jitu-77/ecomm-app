package com.ecommApp.ecomm_app.products.services;

import com.ecommApp.ecomm_app.common.exceptions.ResourceNotFoundException;
import com.ecommApp.ecomm_app.inventory.entity.Inventory;
import com.ecommApp.ecomm_app.inventory.repository.InventoryRepository;
import com.ecommApp.ecomm_app.products.dto.ProductRequest;
import com.ecommApp.ecomm_app.products.dto.ProductResponse;
import com.ecommApp.ecomm_app.products.dto.ProductsByCategoryResponse;
import com.ecommApp.ecomm_app.products.entity.Category;
import com.ecommApp.ecomm_app.products.entity.Product;
import com.ecommApp.ecomm_app.products.mapper.ProductMapper;
import com.ecommApp.ecomm_app.products.repository.CategoryRepository;
import com.ecommApp.ecomm_app.products.repository.ProductRepository;
import com.ecommApp.ecomm_app.user.auth.SecurityUtils;
import com.ecommApp.ecomm_app.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + request.categoryId()
                        )
                );

        Product product = productMapper.toEntity(request);

        product.setCategory(category);
        product.setCreatedBy(currentUser);
        product.setActive(true);

        LocalDateTime now = LocalDateTime.now();

        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        Product savedProduct = productRepository.save(product);

        Inventory inventory = Inventory.builder()
                .product(savedProduct)
                .availableQty(request.initialQty())
                .reservedQty(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        inventoryRepository.save(inventory);

        return buildProductResponse(savedProduct, inventory);
    }

    @Override
    public List<ProductsByCategoryResponse> getAllProducts() {
        List<Category> categories = categoryRepository.findAll();

        List<ProductsByCategoryResponse> response = new ArrayList<>();

        for (Category category : categories) {

            List<Product> products =
                    productRepository.findAllByCategoryId(category.getId());

            List<ProductResponse> productResponses = new ArrayList<>();

            for (Product product : products) {

                Inventory inventory = inventoryRepository.findByProductId(product.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Inventory not found for product id: " + product.getId()
                        ));

                productResponses.add(
                        buildProductResponse(product, inventory)
                );
            }

            response.add(
                    new ProductsByCategoryResponse(
                            category.getId(),
                            category.getName(),
                            productResponses
                    )
            );
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product id: " + productId
                        )
                );

        return buildProductResponse(product, inventory);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + request.categoryId()
                        )
                );

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product id: " + productId
                        )
                );

        return buildProductResponse(updatedProduct, inventory);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product id: " + productId
                        )
                );

        inventoryRepository.delete(inventory);
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductResponse updateInventoryQuantity(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product id: " + productId
                        )
                );

        inventory.setAvailableQty(quantity);
        inventory.setUpdatedAt(LocalDateTime.now());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return buildProductResponse(product, updatedInventory);
    }

    private ProductResponse buildProductResponse(
            Product product,
            Inventory inventory
    ) {

        ProductResponse response = productMapper.toResponse(product);

        return new ProductResponse(
                response.id(),
                response.name(),
                response.description(),
                response.categoryId(),
                response.categoryName(),
                response.price(),
                response.active(),
                inventory.getAvailableQty(),
                inventory.getReservedQty(),
                response.createdBy(),
                response.createdAt(),
                response.updatedAt()
        );
    }
}
