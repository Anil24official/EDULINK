package com.edulink.complianceservice.controller;

import com.edulink.complianceservice.dto.ApiResponse;
import com.edulink.complianceservice.dto.ReportEntityDto;
import com.edulink.complianceservice.service.ReportEntityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/compliance/reports-store")
public class ReportEntityController {

    @Autowired
    private ReportEntityService reportService;

    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','EDUCATION_BOARD_OFFICER','REGULATOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReportEntityDto>>> list(
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String schoolId) {
        return ResponseEntity.ok(ApiResponse.success("Reports fetched",
                reportService.list(scope, schoolId)));
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','EDUCATION_BOARD_OFFICER','REGULATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportEntityDto>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Report fetched", reportService.getById(id)));
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','EDUCATION_BOARD_OFFICER')")
    @PostMapping
    public ResponseEntity<ApiResponse<ReportEntityDto>> create(@Valid @RequestBody ReportEntityDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Report saved", reportService.save(dto)));
    }

    /**
     * Build and persist a fresh snapshot for the given scope. Body fields:
     *   scope, title, schoolId, description (all optional except scope defaults to Compliance).
     */
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','EDUCATION_BOARD_OFFICER')")
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<ReportEntityDto>> generate(@RequestBody Map<String, String> body) {
        String scope = body.getOrDefault("scope", "Compliance");
        String title = body.get("title");
        String schoolId = body.get("schoolId");
        String description = body.get("description");
        return ResponseEntity.ok(ApiResponse.success("Report generated",
                reportService.generate(scope, title, schoolId, description)));
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','EDUCATION_BOARD_OFFICER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportEntityDto>> update(@PathVariable Long id,
                                                                @RequestBody ReportEntityDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Report updated", reportService.update(id, dto)));
    }

    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Report deleted", null));
    }
}
