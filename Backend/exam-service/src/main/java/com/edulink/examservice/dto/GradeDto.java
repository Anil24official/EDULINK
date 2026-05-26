package com.edulink.examservice.dto;

import com.edulink.examservice.entity.Grade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GradeDto {
    private Long id;
    private String courseCode;
    private String examType;
    private String rollNumber;
    private String studentEmail;
    private String teacherEmail;
    private int marksObtained;
    private int totalMarks;
    private int passingMarks;
    private String grade;
    private String remarks;
    private LocalDateTime gradedAt;

    public GradeDto() {}

    public static GradeDto fromEntity(Grade e) {
        if (e == null) return null;
        GradeDto d = new GradeDto();
        d.id = e.getId();
        d.courseCode = e.getCourseCode();
        d.examType = e.getExamType();
        d.rollNumber = e.getRollNumber();
        d.studentEmail = e.getStudentEmail();
        d.teacherEmail = e.getTeacherEmail();
        d.marksObtained = e.getMarksObtained();
        d.totalMarks = e.getTotalMarks();
        d.passingMarks = e.getPassingMarks();
        d.grade = e.getGrade();
        d.remarks = e.getRemarks();
        d.gradedAt = e.getGradedAt();
        return d;
    }

    public static List<GradeDto> fromEntities(List<Grade> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(GradeDto::fromEntity).collect(Collectors.toList());
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
    public String getTeacherEmail() { return teacherEmail; }
    public void setTeacherEmail(String teacherEmail) { this.teacherEmail = teacherEmail; }
    public int getMarksObtained() { return marksObtained; }
    public void setMarksObtained(int marksObtained) { this.marksObtained = marksObtained; }
    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }
    public int getPassingMarks() { return passingMarks; }
    public void setPassingMarks(int passingMarks) { this.passingMarks = passingMarks; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getGradedAt() { return gradedAt; }
    public void setGradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; }
}
