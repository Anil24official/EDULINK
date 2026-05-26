package com.edulink.complianceservice.controller;

import com.edulink.complianceservice.dto.ApiResponse;
import com.edulink.complianceservice.dto.ComplianceRecordDto;
import com.edulink.complianceservice.service.ComplianceRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compliance/records")
public class ComplianceRecordController {

    @Autowired
    private ComplianceRecordService complianceRecordService;

    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','EDUCATION_BOARD_OFFICER','REGULATOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ComplianceRecordDto>>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String schoolId,
            @RequestParam(required = false) String entityId) {
        List<ComplianceRecordDto> rows = complianceRecordService.filter(type, schoolId, entityId);
        return ResponseEntity.ok(ApiResponse.success("Compliance records fetched", rows));
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','EDUCATION_BOARD_OFFICER','REGULATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplianceRecordDto>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Compliance record fetched",
                complianceRecordService.getById(id)));
    }

    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    @PostMapping
    public ResponseEntity<ApiResponse<ComplianceRecordDto>> create(@Valid @RequestBody ComplianceRecordDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Compliance record created",
                complianceRecordService.create(dto)));
    }

    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplianceRecordDto>> update(@PathVariable Long id,
                                                                   @RequestBody ComplianceRecordDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Compliance record updated",
                complianceRecordService.update(id, dto)));
    }

    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        complianceRecordService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Compliance record deleted", null));
    }
}
