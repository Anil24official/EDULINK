package com.edulink.complianceservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class AuditDto {

    private Long id;

    private String officerEmail;

    @NotBlank(message = "scope is required")
    private String scope;

    private String findings;

    private LocalDateTime auditDate;

    private String status;

    private String schoolId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public AuditDto() {}

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
