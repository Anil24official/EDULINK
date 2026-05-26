package com.edulink.attendanceservice.service;

import com.edulink.attendanceservice.dto.PerformanceMetricDto;
import com.edulink.attendanceservice.entity.PerformanceMetric;
import com.edulink.attendanceservice.repository.PerformanceMetricRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PerformanceMetricService {

    private final PerformanceMetricRepository repository;

    public PerformanceMetricService(PerformanceMetricRepository repository) {
        this.repository = repository;
    }

    public PerformanceMetricDto create(PerformanceMetricDto dto) {
        PerformanceMetric pm = new PerformanceMetric();
        pm.setRollNumber(dto.getRollNumber());
        pm.setCourseId(dto.getCourseId());
        pm.setSchoolId(dto.getSchoolId());
        pm.setScore(dto.getScore());
        pm.setMetricType(dto.getMetricType());
        pm.setMetricDate(dto.getMetricDate());
        pm.setStatus(dto.getStatus());
        String me = currentUserEmail();
        pm.setRecordedBy(me != null ? me : dto.getRecordedBy());
        return toDto(repository.save(pm));
    }

    public List<PerformanceMetricDto> filter(String rollNumber, Long courseId, String schoolId) {
        List<PerformanceMetric> rows;
        if (rollNumber != null && courseId != null) {
            rows = repository.findByRollNumberAndCourseIdOrderByMetricDateDesc(rollNumber, courseId);
        } else if (rollNumber != null && !rollNumber.isBlank()) {
            rows = repository.findByRollNumberOrderByMetricDateDesc(rollNumber);
        } else if (courseId != null) {
            rows = repository.findByCourseIdOrderByMetricDateDesc(courseId);
        } else if (schoolId != null && !schoolId.isBlank()) {
            rows = repository.findBySchoolIdOrderByMetricDateDesc(schoolId);
        } else {
            rows = repository.findAll();
        }
        return rows.stream().map(this::toDto).collect(Collectors.toList());
    }

    public PerformanceMetricDto getById(Long id) {
        return toDto(repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Performance metric not found: " + id)));
    }

    public PerformanceMetricDto update(Long id, PerformanceMetricDto dto) {
        PerformanceMetric pm = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Performance metric not found: " + id));
        if (dto.getScore() != null) pm.setScore(dto.getScore());
        if (dto.getMetricType() != null) pm.setMetricType(dto.getMetricType());
        if (dto.getMetricDate() != null) pm.setMetricDate(dto.getMetricDate());
        if (dto.getStatus() != null) pm.setStatus(dto.getStatus());
        if (dto.getSchoolId() != null) pm.setSchoolId(dto.getSchoolId());
        if (dto.getCourseId() != null) pm.setCourseId(dto.getCourseId());
        if (dto.getRollNumber() != null) pm.setRollNumber(dto.getRollNumber());
        return toDto(repository.save(pm));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Performance metric not found: " + id);
        }
        repository.deleteById(id);
    }

    private PerformanceMetricDto toDto(PerformanceMetric pm) {
        PerformanceMetricDto d = new PerformanceMetricDto();
        d.setId(pm.getId());
        d.setRollNumber(pm.getRollNumber());
        d.setCourseId(pm.getCourseId());
        d.setSchoolId(pm.getSchoolId());
        d.setScore(pm.getScore());
        d.setMetricType(pm.getMetricType());
        d.setMetricDate(pm.getMetricDate());
        d.setStatus(pm.getStatus());
        d.setRecordedBy(pm.getRecordedBy());
        d.setCreatedAt(pm.getCreatedAt());
        return d;
    }

    private String currentUserEmail() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getName();
            return principal != null ? principal.toString() : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
