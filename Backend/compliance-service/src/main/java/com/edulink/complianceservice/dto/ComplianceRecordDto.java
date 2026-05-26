package com.edulink.complianceservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class ComplianceRecordDto {

    private Long id;

    @NotBlank(message = "entityId is required")
    private String entityId;

    @NotBlank(message = "type is required (Course | Exam | Attendance)")
    private String type;

    @NotBlank(message = "result is required")
    private String result;

    private String notes;

    private String recordedByEmail;

    private String schoolId;

    private LocalDateTime recordedDate;

    private LocalDateTime createdAt;

    public ComplianceRecordDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getRecordedByEmail() { return recordedByEmail; }
    public void setRecordedByEmail(String recordedByEmail) { this.recordedByEmail = recordedByEmail; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }

    public LocalDateTime getRecordedDate() { return recordedDate; }
    public void setRecordedDate(LocalDateTime recordedDate) { this.recordedDate = recordedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
