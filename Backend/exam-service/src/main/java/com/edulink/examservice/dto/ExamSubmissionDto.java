package com.edulink.examservice.dto;

import com.edulink.examservice.entity.ExamSubmission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ExamSubmissionDto {
    private Long id;
    private String courseCode;
    private String examType;
    private String rollNumber;
    private String studentEmail;
    private String submissionContent;
    private String submissionFileId;
    private LocalDateTime submittedAt;
    private boolean isLate;
    private LocalDateTime startedAt;

    public ExamSubmissionDto() {}

    public static ExamSubmissionDto fromEntity(ExamSubmission e) {
        if (e == null) return null;
        ExamSubmissionDto d = new ExamSubmissionDto();
        d.id = e.getId();
        d.courseCode = e.getCourseCode();
        d.examType = e.getExamType();
        d.rollNumber = e.getRollNumber();
        d.studentEmail = e.getStudentEmail();
        d.submissionContent = e.getSubmissionContent();
        d.submissionFileId = e.getSubmissionFileId();
        d.submittedAt = e.getSubmittedAt();
        d.isLate = e.isLate();
        d.startedAt = e.getStartedAt();
        return d;
    }

    public static List<ExamSubmissionDto> fromEntities(List<ExamSubmission> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(ExamSubmissionDto::fromEntity).collect(Collectors.toList());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public String getSubmissionContent() { return submissionContent; }
    public void setSubmissionContent(String submissionContent) { this.submissionContent = submissionContent; }
    public String getSubmissionFileId() { return submissionFileId; }
    public void setSubmissionFileId(String submissionFileId) { this.submissionFileId = submissionFileId; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public boolean isLate() { return isLate; }
    public void setLate(boolean late) { isLate = late; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
}
