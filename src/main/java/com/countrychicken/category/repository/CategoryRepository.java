package com.countrychicken.category.repository;

import com.countrychicken.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsBySlugIgnoreCase(String slug);
}
