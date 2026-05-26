package com.edulink.examservice.dto;

import com.edulink.examservice.entity.Exam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ExamDto {
    private Long id;
    private String courseCode;
    private String teacherEmail;
    private String examTitle;
    private String examType;
    private int totalMarks;
    private int passingMarks;
    private String schoolId;
    private String questionsFileId;
    private LocalDateTime examDate;
    private LocalDateTime createdAt;
    private Integer durationMinutes;

    public ExamDto() {}

    public static ExamDto fromEntity(Exam e) {
        if (e == null) return null;
        ExamDto d = new ExamDto();
        d.id = e.getId();
        d.courseCode = e.getCourseCode();
        d.teacherEmail = e.getTeacherEmail();
        d.examTitle = e.getExamTitle();
        d.examType = e.getExamType();
        d.totalMarks = e.getTotalMarks();
        d.passingMarks = e.getPassingMarks();
        d.schoolId = e.getSchoolId();
        d.questionsFileId = e.getQuestionsFileId();
        d.examDate = e.getExamDate();
        d.createdAt = e.getCreatedAt();
        d.durationMinutes = e.getDurationMinutes();
        return d;
    }

    public static List<ExamDto> fromEntities(List<Exam> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(ExamDto::fromEntity).collect(Collectors.toList());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getTeacherEmail() { return teacherEmail; }
    public void setTeacherEmail(String teacherEmail) { this.teacherEmail = teacherEmail; }
    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }
    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }
    public int getPassingMarks() { return passingMarks; }
    public void setPassingMarks(int passingMarks) { this.passingMarks = passingMarks; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public String getQuestionsFileId() { return questionsFileId; }
    public void setQuestionsFileId(String questionsFileId) { this.questionsFileId = questionsFileId; }
    public LocalDateTime getExamDate() { return examDate; }
    public void setExamDate(LocalDateTime examDate) { this.examDate = examDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}
