package com.edulink.identityservice.controller;

import com.edulink.identityservice.dto.ApiResponse;
import com.edulink.identityservice.dto.AuditIngestRequest;
import com.edulink.identityservice.dto.AuditLogDto;
import com.edulink.identityservice.service.AuditLogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PreAuthorize("hasRole('OPERATOR')")
    @GetMapping("/operator/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLogDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String action) {
        Page<AuditLogDto> result = auditLogService.list(page, size, userEmail, action);
        return ResponseEntity.ok(ApiResponse.success("Audit logs fetched", result));
    }

    /**
     * Internal endpoint for sibling microservices to append audit entries.
     * Accepts any authenticated principal (caller passes its own JWT through the gateway).
     * Spec §8 — append-only is enforced by the entity/repository layer.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/internal/audit/record")
    public ResponseEntity<ApiResponse<Void>> record(@Valid @RequestBody AuditIngestRequest req) {
        String status = (req.getStatus() == null || req.getStatus().isBlank()) ? "SUCCESS" : req.getStatus();
        auditLogService.record(req.getUserEmail(), req.getUserRole(), req.getAction(),
                req.getResource(), req.getDetails(), status);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success("Recorded", null));
    }
}
