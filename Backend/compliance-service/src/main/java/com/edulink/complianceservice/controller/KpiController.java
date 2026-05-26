package com.edulink.complianceservice.controller;

import com.edulink.complianceservice.dto.ApiResponse;
import com.edulink.complianceservice.dto.KpiDto;
import com.edulink.complianceservice.service.KpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class KpiController {

    @Autowired
    private KpiService kpiService;

    /** Compliance-only KPIs (locally computed from rule/audit/record tables). */
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER','EDUCATION_BOARD_OFFICER','REGULATOR')")
    @GetMapping("/compliance/kpis")
    public ResponseEntity<ApiResponse<KpiDto>> complianceKpis(@RequestParam(required = false) String schoolId) {
        return ResponseEntity.ok(ApiResponse.success("Compliance KPIs",
                kpiService.getComplianceKpis(schoolId)));
    }

    /**
     * Aggregator: exam pass rate + attendance % + compliance adherence
     * (PDF §8 named KPIs). Pulls exam-service and attendance-service via Feign.
     */
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','EDUCATION_BOARD_OFFICER','REGULATOR','COMPLIANCE_OFFICER')")
    @GetMapping("/admin/kpis")
    public ResponseEntity<ApiResponse<KpiDto>> aggregatedKpis(@RequestParam(required = false) String schoolId) {
        return ResponseEntity.ok(ApiResponse.success("Aggregated KPIs",
                kpiService.getAggregatedKpis(schoolId)));
    }
}
