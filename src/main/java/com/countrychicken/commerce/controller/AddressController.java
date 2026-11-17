package com.countrychicken.commerce.controller;
import com.countrychicken.commerce.dto.*;
import com.countrychicken.commerce.service.AddressService;
import com.countrychicken.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/account/addresses")
public class AddressController {
    private final AddressService service;
    public AddressController(AddressService service) { this.service = service; }
    @GetMapping public ResponseEntity<ApiResponse<List<AddressResponse>>> list(Authentication auth, HttpServletRequest r) { return ResponseEntity.ok(ApiResponse.success(service.list(userId(auth)), "Addresses retrieved", r.getRequestURI())); }
    @PostMapping public ResponseEntity<ApiResponse<AddressResponse>> create(Authentication auth, @Valid @RequestBody AddressRequest body, HttpServletRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.create(userId(auth), body), "Address created", r.getRequestURI())); }
    @PutMapping("/{id}") public ResponseEntity<ApiResponse<AddressResponse>> update(Authentication auth, @PathVariable UUID id, @Valid @RequestBody AddressRequest body, HttpServletRequest r) { return ResponseEntity.ok(ApiResponse.success(service.update(userId(auth), id, body), "Address updated", r.getRequestURI())); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(Authentication auth, @PathVariable UUID id) { service.delete(userId(auth), id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/default") public ResponseEntity<ApiResponse<AddressResponse>> setDefault(Authentication auth, @PathVariable UUID id, HttpServletRequest r) { return ResponseEntity.ok(ApiResponse.success(service.setDefault(userId(auth), id), "Default address updated", r.getRequestURI())); }
    private UUID userId(Authentication auth) { return UUID.fromString(auth.getName()); }
}
