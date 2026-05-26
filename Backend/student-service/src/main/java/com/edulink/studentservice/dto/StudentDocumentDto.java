package com.edulink.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDocumentDto {
    private Long id;
    private String studentEmail;
    private Long studentId;
    private String docType;
    private String fileId;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String verificationStatus;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private String verificationNotes;
    private LocalDateTime uploadedDate;
}
