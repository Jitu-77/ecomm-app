package com.ecommApp.ecomm_app.products.repository;

import com.ecommApp.ecomm_app.products.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}