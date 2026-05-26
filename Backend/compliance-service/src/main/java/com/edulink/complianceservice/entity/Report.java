package com.edulink.complianceservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_report_scope", columnList = "scope"),
        @Index(name = "idx_report_school", columnList = "schoolId"),
        @Index(name = "idx_report_generated", columnList = "generatedDate")
})
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String scope;

    private String schoolId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String metrics;

    private String generatedByEmail;

    private LocalDateTime generatedDate;

    private String status;

    @PrePersist
    protected void onCreate() {
        generatedDate = LocalDateTime.now();
        if (status == null || status.isBlank()) {
            status = "GENERATED";
        }
    }

    public Report() {}

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
