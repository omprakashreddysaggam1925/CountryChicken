package com.countrychicken.commerce.controller;
import com.countrychicken.commerce.dto.*;
import com.countrychicken.commerce.service.CartService;
import com.countrychicken.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService service;
    public CartController(CartService service) { this.service = service; }
    @GetMapping public ResponseEntity<ApiResponse<CartResponse>> get(Authentication auth, HttpServletRequest r) { return ResponseEntity.ok(ApiResponse.success(service.get(id(auth)), "Cart retrieved", r.getRequestURI())); }
    @PostMapping("/items") public ResponseEntity<ApiResponse<CartResponse>> add(Authentication auth, @Valid @RequestBody CartItemRequest body, HttpServletRequest r) { return ResponseEntity.ok(ApiResponse.success(service.add(id(auth), body), "Item added to cart", r.getRequestURI())); }
    @PutMapping("/items/{variantId}") public ResponseEntity<ApiResponse<CartResponse>> update(Authentication auth, @PathVariable UUID variantId, @Valid @RequestBody CartItemRequest body, HttpServletRequest r) { return ResponseEntity.ok(ApiResponse.success(service.update(id(auth), variantId, body), "Cart item updated", r.getRequestURI())); }
    @DeleteMapping("/items/{variantId}") public ResponseEntity<ApiResponse<CartResponse>> remove(Authentication auth, @PathVariable UUID variantId, HttpServletRequest r) { return ResponseEntity.ok(ApiResponse.success(service.remove(id(auth), variantId), "Item removed from cart", r.getRequestURI())); }
    private UUID id(Authentication auth) { return UUID.fromString(auth.getName()); }
}
