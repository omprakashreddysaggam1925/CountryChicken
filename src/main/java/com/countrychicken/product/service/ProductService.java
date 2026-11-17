package com.countrychicken.product.service;

import com.countrychicken.category.entity.Category;
import com.countrychicken.category.repository.CategoryRepository;
import com.countrychicken.category.service.CategoryService;
import com.countrychicken.common.exception.ConflictException;
import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.product.dto.*;
import com.countrychicken.product.entity.*;
import com.countrychicken.product.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository products;
    private final ProductVariantRepository variants;
    private final CategoryRepository categories;

    public ProductService(ProductRepository products, ProductVariantRepository variants, CategoryRepository categories) {
        this.products = products; this.variants = variants; this.categories = categories;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> listPublic(String search, Pageable pageable) {
        Page<Product> page = search == null || search.isBlank()
                ? products.findByStatusAndCategoryActiveTrue(ProductStatus.ACTIVE, pageable)
                : products.findByStatusAndCategoryActiveTrueAndNameContainingIgnoreCase(ProductStatus.ACTIVE, search.trim(), pageable);
        return page.map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public ProductResponse getPublic(UUID id) {
        Product product = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product was not found"));
        if (product.getStatus() != ProductStatus.ACTIVE || !product.getCategory().isActive())
            throw new ResourceNotFoundException("Product was not found");
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = category(request.categoryId());
        String slug = normalizeSlug(request.slug(), request.name());
        if (products.existsBySlugIgnoreCase(slug)) throw new ConflictException("A product already exists with this slug");
        return ProductResponse.from(products.save(new Product(category, request.name().trim(), slug,
                normalize(request.description()), request.status())));
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product was not found"));
        String slug = normalizeSlug(request.slug(), request.name());
        if (!product.getSlug().equalsIgnoreCase(slug) && products.existsBySlugIgnoreCase(slug))
            throw new ConflictException("A product already exists with this slug");
        product.update(category(request.categoryId()), request.name().trim(), slug, normalize(request.description()), request.status());
        return ProductResponse.from(product);
    }

    @Transactional
    public VariantResponse addVariant(UUID productId, VariantRequest request) {
        Product product = products.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product was not found"));
        String sku = request.sku().trim().toUpperCase(Locale.ROOT);
        if (variants.existsBySkuIgnoreCase(sku)) throw new ConflictException("A variant already exists with this SKU");
        ProductVariant variant = new ProductVariant(sku, request.weightGrams(), request.price());
        product.addVariant(variant);
        return VariantResponse.from(variants.save(variant));
    }

    @Transactional
    public void delete(UUID id) {
        if (!products.existsById(id)) throw new ResourceNotFoundException("Product was not found");
        products.deleteById(id);
    }
    private Category category(UUID id) { return categories.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category was not found")); }
    private String normalizeSlug(String value, String fallback) { return CategoryService.slug(value == null || value.isBlank() ? fallback : value); }
    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
