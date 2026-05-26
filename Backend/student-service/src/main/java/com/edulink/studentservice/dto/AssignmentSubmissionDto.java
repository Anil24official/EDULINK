package com.edulink.studentservice.dto;

import com.edulink.studentservice.entity.AssignmentSubmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmissionDto {
    private Long id;
    private Long studentId;
    private Integer assignmentNum;
    private String assignmentTitle;
    private String courseCode;
    private String studentEmail;
    private String rollNumber;
    private String submissionContent;
    private String fileId;
    private String fileName;
    private String status;
    private LocalDateTime submittedAt;
    private Integer marksObtained;
    private Integer maxMarks;
    private String remarks;
    private String evaluatedBy;
    private LocalDateTime evaluatedAt;

    public static AssignmentSubmissionDto fromEntity(AssignmentSubmission e) {
        if (e == null) return null;
        return AssignmentSubmissionDto.builder()
                .id(e.getId())
                .studentId(e.getStudentId())
                .assignmentNum(e.getAssignmentNum())
                .assignmentTitle(e.getAssignmentTitle())
                .courseCode(e.getCourseCode())
                .studentEmail(e.getStudentEmail())
                .rollNumber(e.getRollNumber())
                .submissionContent(e.getSubmissionContent())
                .fileId(e.getFileId())
                .fileName(e.getFileName())
                .status(e.getStatus())
                .submittedAt(e.getSubmittedAt())
                .marksObtained(e.getMarksObtained())
                .maxMarks(e.getMaxMarks())
                .remarks(e.getRemarks())
                .evaluatedBy(e.getEvaluatedBy())
                .evaluatedAt(e.getEvaluatedAt())
                .build();
    }

    public static List<AssignmentSubmissionDto> fromEntities(List<AssignmentSubmission> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(AssignmentSubmissionDto::fromEntity).collect(Collectors.toList());
    }
}
