package com.edulink.attendanceservice.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PerformanceMetricDto {

    private Long id;

    @NotBlank
    private String rollNumber;

    @NotNull
    @Positive
    private Long courseId;

    private String schoolId;

    @NotNull
    @DecimalMin("0.0")
    private Double score;

    @NotBlank
    private String metricType;

    @NotNull
    private LocalDate metricDate;

    private String status;

    private String recordedBy;

    private LocalDateTime createdAt;

    public PerformanceMetricDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }

    public LocalDate getMetricDate() { return metricDate; }
    public void setMetricDate(LocalDate metricDate) { this.metricDate = metricDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
