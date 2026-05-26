package com.edulink.complianceservice.client;

import com.edulink.complianceservice.config.FeignClientConfig;
import com.edulink.complianceservice.dto.ApiResponse;
import com.edulink.complianceservice.dto.ExamKpiDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "EXAM-SERVICE", configuration = FeignClientConfig.class)
public interface ExamKpiClient {

    @GetMapping("/exam/kpis")
    ResponseEntity<ApiResponse<ExamKpiDto>> kpis();
}
