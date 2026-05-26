package com.edulink.complianceservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class ReportEntityDto {

    private Long id;

    @NotBlank(message = "scope is required (Student | Course | Exam | Attendance | Compliance)")
    private String scope;

    private String schoolId;

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    private String metrics;

    private String generatedByEmail;

    private LocalDateTime generatedDate;

    private String status;

    public ReportEntityDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMetrics() { return metrics; }
    public void setMetrics(String metrics) { this.metrics = metrics; }

    public String getGeneratedByEmail() { return generatedByEmail; }
    public void setGeneratedByEmail(String generatedByEmail) { this.generatedByEmail = generatedByEmail; }

    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
