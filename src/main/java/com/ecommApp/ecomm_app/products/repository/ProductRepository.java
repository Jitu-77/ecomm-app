package com.ecommApp.ecomm_app.products.repository;

import com.ecommApp.ecomm_app.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}