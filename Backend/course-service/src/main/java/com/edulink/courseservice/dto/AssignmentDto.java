package com.edulink.courseservice.dto;

import com.edulink.courseservice.entity.Assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentDto {
    private Long id;
    private Integer assignmentNum;
    private String courseCode;
    private String teacherEmail;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private int maxMarks;
    private LocalDateTime createdAt;
    private String questionsFileId;

    public AssignmentDto() {}

    public static AssignmentDto fromEntity(Assignment a) {
        if (a == null) return null;
        AssignmentDto d = new AssignmentDto();
        d.id = a.getId();
        d.assignmentNum = a.getAssignmentNum();
        d.courseCode = a.getCourseCode();
        d.teacherEmail = a.getTeacherEmail();
        d.title = a.getTitle();
        d.description = a.getDescription();
        d.dueDate = a.getDueDate();
        d.maxMarks = a.getMaxMarks();
        d.createdAt = a.getCreatedAt();
        d.questionsFileId = a.getQuestionsFileId();
        return d;
    }

    public static List<AssignmentDto> fromEntities(List<Assignment> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(AssignmentDto::fromEntity).collect(Collectors.toList());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getAssignmentNum() { return assignmentNum; }
    public void setAssignmentNum(Integer assignmentNum) { this.assignmentNum = assignmentNum; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getTeacherEmail() { return teacherEmail; }
    public void setTeacherEmail(String teacherEmail) { this.teacherEmail = teacherEmail; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getQuestionsFileId() { return questionsFileId; }
    public void setQuestionsFileId(String questionsFileId) { this.questionsFileId = questionsFileId; }
}
