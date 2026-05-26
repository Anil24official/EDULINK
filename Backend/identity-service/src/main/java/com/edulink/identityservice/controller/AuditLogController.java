package com.edulink.identityservice.controller;

import com.edulink.identityservice.dto.ApiResponse;
import com.edulink.identityservice.dto.AuditLogDto;
import com.edulink.identityservice.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operator/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PreAuthorize("hasRole('OPERATOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String action) {
        Page<AuditLogDto> result = auditLogService.list(page, size, userEmail, action);
        return ResponseEntity.ok(ApiResponse.success("Audit logs fetched", result));
    }
}
