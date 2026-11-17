package com.countrychicken.category.service;

import com.countrychicken.category.dto.*;
import com.countrychicken.category.entity.Category;
import com.countrychicken.category.repository.CategoryRepository;
import com.countrychicken.common.exception.ConflictException;
import com.countrychicken.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(boolean includeInactive) {
        return repository.findAll().stream()
                .filter(category -> includeInactive || category.isActive())
                .map(CategoryResponse::from).toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String slug = slug(request.name());
        if (repository.existsBySlugIgnoreCase(slug)) throw new ConflictException("A category already exists with this name");
        validateParent(request.parentId());
        return CategoryResponse.from(repository.save(new Category(request.name().trim(), slug,
                normalize(request.description()), request.parentId())));
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category was not found"));
        String slug = slug(request.name());
        if (!category.getSlug().equalsIgnoreCase(slug) && repository.existsBySlugIgnoreCase(slug))
            throw new ConflictException("A category already exists with this name");
        validateParent(request.parentId());
        category.update(request.name().trim(), slug, normalize(request.description()), request.parentId(),
                request.active() == null || request.active());
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Category was not found");
        repository.deleteById(id);
    }

    private void validateParent(UUID parentId) {
        if (parentId != null && !repository.existsById(parentId))
            throw new ResourceNotFoundException("Parent category was not found");
    }
    public static String slug(String value) {
        return Normalizer.normalize(value.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
