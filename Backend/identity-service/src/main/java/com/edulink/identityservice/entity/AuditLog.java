package com.edulink.identityservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Spec §8 — immutable audit log. Once persisted, rows cannot be updated or deleted.
 * Hibernate-level enforcement is provided by {@code @PreUpdate} / {@code @PreRemove}
 * which throw. The repository (see AuditLogRepository) also forbids delete/save-after-id.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_user_email", columnList = "userEmail"),
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private String userEmail;

    @Column(updatable = false)
    private String userRole;

    @Column(updatable = false)
    private String action;

    @Column(updatable = false)
    private String resource;

    @Column(columnDefinition = "TEXT", updatable = false)
    private String details;

    @Column(updatable = false)
    private String status;

    @Column(updatable = false)
    private String ipAddress;

    @Column(updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
        if (status == null || status.isBlank()) {
            status = "SUCCESS";
        }
    }

    @PreUpdate
    protected void rejectUpdate() {
        throw new UnsupportedOperationException("AuditLog is immutable (spec §8). Rows cannot be modified after insert.");
    }

    @PreRemove
    protected void rejectRemove() {
        throw new UnsupportedOperationException("AuditLog is immutable (spec §8). Rows cannot be deleted.");
    }

    public AuditLog() {}

    public AuditLog(String userEmail, String userRole, String action, String resource,
                    String details, String status, String ipAddress) {
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.action = action;
        this.resource = resource;
        this.details = details;
        this.status = status;
        this.ipAddress = ipAddress;
    }

    public Long getId() { return id; }

    public String getUserEmail() { return userEmail; }

    public String getUserRole() { return userRole; }

    public String getAction() { return action; }

    public String getResource() { return resource; }

    public String getDetails() { return details; }

    public String getStatus() { return status; }

    public String getIpAddress() { return ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
}
