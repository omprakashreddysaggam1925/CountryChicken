package com.countrychicken.notification.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.notification.dto.NotificationResponse;
import com.countrychicken.notification.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/account/notifications")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> list(Authentication auth, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.list(UUID.fromString(auth.getName())), "Notifications retrieved", request.getRequestURI()));
    }
}
