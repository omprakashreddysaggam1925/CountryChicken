package com.countrychicken.product.repository;

import com.countrychicken.product.entity.Product;
import com.countrychicken.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySlugIgnoreCase(String slug);
    Page<Product> findByStatusAndCategoryActiveTrue(ProductStatus status, Pageable pageable);
    Page<Product> findByStatusAndCategoryActiveTrueAndNameContainingIgnoreCase(
            ProductStatus status, String name, Pageable pageable);
    long countByStatus(ProductStatus status);
}
