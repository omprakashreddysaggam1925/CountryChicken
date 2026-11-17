package com.countrychicken.category.controller;

import com.countrychicken.category.dto.*;
import com.countrychicken.category.service.CategoryService;
import com.countrychicken.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> list(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.list(false), "Categories retrieved", request.getRequestURI()));
    }

    @GetMapping("/admin/categories")
    @PreAuthorize("hasAuthority('CATEGORY_READ')")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> adminList(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.list(true), "Categories retrieved", request.getRequestURI()));
    }

    @PostMapping("/admin/categories")
    @PreAuthorize("hasAuthority('CATEGORY_CREATE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest body, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.create(body), "Category created", request.getRequestURI()));
    }

    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_UPDATE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest body, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, body), "Category updated", request.getRequestURI()));
    }

    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
