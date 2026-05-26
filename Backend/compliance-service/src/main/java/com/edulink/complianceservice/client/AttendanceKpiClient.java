package com.edulink.complianceservice.client;

import com.edulink.complianceservice.config.FeignClientConfig;
import com.edulink.complianceservice.dto.ApiResponse;
import com.edulink.complianceservice.dto.AttendanceKpiDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ATTENDANCE-SERVICE", configuration = FeignClientConfig.class)
public interface AttendanceKpiClient {

    @GetMapping("/attendance/kpis")
    ResponseEntity<ApiResponse<AttendanceKpiDto>> kpis(@RequestParam(value = "schoolId", required = false) String schoolId);
}
