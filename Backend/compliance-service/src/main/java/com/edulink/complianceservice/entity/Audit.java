package com.edulink.complianceservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audits")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String officerEmail;

    private String scope;

    @Column(columnDefinition = "TEXT")
    private String findings;

    private LocalDateTime auditDate;

    private String status;

    private String schoolId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (auditDate == null) {
            auditDate = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = "OPEN";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Audit() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOfficerEmail() { return officerEmail; }
    public void setOfficerEmail(String officerEmail) { this.officerEmail = officerEmail; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getFindings() { return findings; }
    public void setFindings(String findings) { this.findings = findings; }

    public LocalDateTime getAuditDate() { return auditDate; }
    public void setAuditDate(LocalDateTime auditDate) { this.auditDate = auditDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
