package com.edulink.studentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_documents", indexes = {
        @Index(name = "idx_doc_student_email", columnList = "studentEmail"),
        @Index(name = "idx_doc_type", columnList = "docType")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentEmail;

    private Long studentId;

    @Column(nullable = false)
    private String docType;

    @Column(nullable = false)
    private String fileId;

    private String fileName;

    private String contentType;

    private Long fileSize;

    @Column(nullable = false)
    private String verificationStatus;

    private String verifiedBy;

    private LocalDateTime verifiedAt;

    @Column(columnDefinition = "TEXT")
    private String verificationNotes;

    private LocalDateTime uploadedDate;

    @PrePersist
    protected void onCreate() {
        uploadedDate = LocalDateTime.now();
        if (verificationStatus == null || verificationStatus.isBlank()) {
            verificationStatus = "PENDING";
        }
    }
}
