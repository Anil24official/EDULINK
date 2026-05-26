package com.edulink.attendanceservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance_metrics", indexes = {
        @Index(name = "idx_pm_roll", columnList = "rollNumber"),
        @Index(name = "idx_pm_course", columnList = "courseId"),
        @Index(name = "idx_pm_school", columnList = "schoolId")
})
public class PerformanceMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Roll number is required")
    @Pattern(regexp = "^[A-Z]{3}\\d{6}$",
            message = "Roll number must follow pattern: 3 uppercase letters + 6 digits")
    @Column(nullable = false)
    private String rollNumber;

    @NotNull(message = "Course ID is required")
    @Positive
    @Column(nullable = false)
    private Long courseId;

    @Size(max = 20)
    private String schoolId;

    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", message = "Score cannot be negative")
    @Column(nullable = false)
    private Double score;

    @NotBlank(message = "Metric type is required")
    @Column(nullable = false)
    private String metricType;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDate metricDate;

    @Column(nullable = false)
    private String status;

    private String recordedBy;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null || status.isBlank()) {
            status = "ACTIVE";
        }
    }

    public PerformanceMetric() {}

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
