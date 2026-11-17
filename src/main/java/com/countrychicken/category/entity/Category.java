package com.countrychicken.category.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category extends AbstractEntity {

    @Column(name = "parent_id")
    private java.util.UUID parentId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 180)
    private String slug;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active;

    protected Category() {
    }

    public Category(String name, String slug, String description, java.util.UUID parentId) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.parentId = parentId;
        this.active = true;
    }

    public java.util.UUID getParentId() { return parentId; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }

    public void update(String name, String slug, String description, java.util.UUID parentId, boolean active) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.parentId = parentId;
        this.active = active;
    }
}
