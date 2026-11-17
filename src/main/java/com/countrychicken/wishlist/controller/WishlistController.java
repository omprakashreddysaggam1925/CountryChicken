package com.countrychicken.wishlist.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.wishlist.dto.WishlistResponse;
import com.countrychicken.wishlist.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/account/wishlist")
public class WishlistController {
    private final WishlistService service;
    public WishlistController(WishlistService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> list(Authentication auth, HttpServletRequest r) {
        return ResponseEntity.ok(ApiResponse.success(service.list(id(auth)), "Wishlist retrieved", r.getRequestURI()));
    }
    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<WishlistResponse>> add(Authentication auth, @PathVariable UUID productId, HttpServletRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.add(id(auth), productId), "Product added to wishlist", r.getRequestURI()));
    }
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(Authentication auth, @PathVariable UUID productId) {
        service.remove(id(auth), productId); return ResponseEntity.noContent().build();
    }
    private UUID id(Authentication auth) { return UUID.fromString(auth.getName()); }
}
