package com.edulink.attendanceservice.controller;

import com.edulink.attendanceservice.dto.ApiResponse;
import com.edulink.attendanceservice.dto.PerformanceMetricDto;
import com.edulink.attendanceservice.service.PerformanceMetricService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PerformanceMetricController {

    private final PerformanceMetricService service;

    public PerformanceMetricController(PerformanceMetricService service) {
        this.service = service;
    }

    /* ─── Teacher: create / update / delete ─── */
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/teacher/performance-metrics")
    public ResponseEntity<ApiResponse<PerformanceMetricDto>> create(@Valid @RequestBody PerformanceMetricDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Performance metric recorded", service.create(dto)));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/teacher/performance-metrics/{id}")
    public ResponseEntity<ApiResponse<PerformanceMetricDto>> update(@PathVariable Long id,
                                                                    @RequestBody PerformanceMetricDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Performance metric updated", service.update(id, dto)));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/teacher/performance-metrics/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Performance metric deleted", null));
    }

    /* ─── Teacher: list/filter (course-wide view) ─── */
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN','EDUCATION_BOARD_OFFICER')")
    @GetMapping("/teacher/performance-metrics")
    public ResponseEntity<ApiResponse<List<PerformanceMetricDto>>> listForTeacher(
            @RequestParam(required = false) String rollNumber,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String schoolId) {
        return ResponseEntity.ok(ApiResponse.success("Performance metrics fetched",
                service.filter(rollNumber, courseId, schoolId)));
    }

    /* ─── Admin: school-wide list ─── */
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','EDUCATION_BOARD_OFFICER')")
    @GetMapping("/admin/performance-metrics")
    public ResponseEntity<ApiResponse<List<PerformanceMetricDto>>> listForAdmin(
            @RequestParam(required = false) String rollNumber,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String schoolId) {
        return ResponseEntity.ok(ApiResponse.success("Performance metrics fetched",
                service.filter(rollNumber, courseId, schoolId)));
    }

    /* ─── Student: own metrics ─── */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/performance-metrics")
    public ResponseEntity<ApiResponse<List<PerformanceMetricDto>>> myMetrics(
            @RequestParam(required = false) Long courseId) {
        String rollNumber = currentRollNumber();
        if (rollNumber == null) {
            return ResponseEntity.ok(ApiResponse.success("No roll number in token", List.of()));
        }
        return ResponseEntity.ok(ApiResponse.success("Performance metrics fetched",
                service.filter(rollNumber, courseId, null)));
    }

    private String currentRollNumber() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object details = auth.getDetails();
        return details != null ? details.toString() : null;
    }
}
