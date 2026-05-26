package com.edulink.complianceservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_records")
public class ComplianceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityId;

    private String type;

    private String result;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private String recordedByEmail;

    private String schoolId;

    private LocalDateTime recordedDate;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (recordedDate == null) {
            recordedDate = LocalDateTime.now();
        }
    }

    public ComplianceRecord() {}

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
