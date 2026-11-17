package com.countrychicken.product.entity;

import com.countrychicken.category.entity.Category;
import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ProductStatus status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    protected Product() {
    }

    public Product(Category category, String name, String slug, String description, ProductStatus status) {
        this.category = category;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.status = status;
    }

    public Category getCategory() { return category; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public ProductStatus getStatus() { return status; }
    public List<ProductVariant> getVariants() { return variants; }

    public void update(Category category, String name, String slug, String description, ProductStatus status) {
        this.category = category;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.status = status;
    }

    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.assignProduct(this);
    }
}
