package com.countrychicken.reporting.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.reporting.dto.DashboardResponse;
import com.countrychicken.reporting.service.ReportingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reports")
public class ReportingController {
    private final ReportingService service;
    public ReportingController(ReportingService service) { this.service = service; }
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<ApiResponse<DashboardResponse>> dashboard(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.dashboard(), "Dashboard metrics retrieved", request.getRequestURI()));
    }
}
