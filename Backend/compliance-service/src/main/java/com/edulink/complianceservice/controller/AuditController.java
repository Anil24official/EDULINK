package com.edulink.complianceservice.controller;

import com.edulink.complianceservice.dto.ApiResponse;
import com.edulink.complianceservice.dto.AuditDto;
import com.edulink.complianceservice.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compliance/audits")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','EDUCATION_BOARD_OFFICER','REGULATOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditDto>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String schoolId,
            @RequestParam(required = false) String officerEmail) {
        return ResponseEntity.ok(ApiResponse.success("Audits fetched",
                auditService.filter(status, schoolId, officerEmail)));
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','EDUCATION_BOARD_OFFICER','REGULATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditDto>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Audit fetched", auditService.getById(id)));
    }

    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    @PostMapping
    public ResponseEntity<ApiResponse<AuditDto>> create(@Valid @RequestBody AuditDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Audit created", auditService.create(dto)));
    }

    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditDto>> update(@PathVariable Long id,
                                                        @RequestBody AuditDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Audit updated", auditService.update(id, dto)));
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','REGULATOR')")
    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<ApiResponse<AuditDto>> updateStatus(@PathVariable Long id,
                                                              @PathVariable String status) {
        return ResponseEntity.ok(ApiResponse.success("Audit status updated",
                auditService.updateStatus(id, status)));
    }

    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        auditService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Audit deleted", null));
    }
}
