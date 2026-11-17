package com.countrychicken.permission.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "permissions")
public class Permission extends AbstractEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String resource;

    @Column(nullable = false, length = 32)
    private String operation;

    @Column(length = 500)
    private String description;

    protected Permission() {
    }

    public Permission(String name, String resource, String operation, String description) {
        this.name = name;
        this.resource = resource;
        this.operation = operation;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getResource() {
        return resource;
    }

    public String getOperation() {
        return operation;
    }

    public String getDescription() {
        return description;
    }

    public void update(String name, String resource, String operation, String description) {
        this.name = name;
        this.resource = resource;
        this.operation = operation;
        this.description = description;
    }
}
