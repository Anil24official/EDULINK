package com.edulink.courseservice.dto;

import com.edulink.courseservice.entity.Course;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CourseDto {
    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    private Long classId;
    private String schoolId;
    private Long teacherId;
    private String subject;
    private String grade;
    private boolean active;
    private LocalDateTime createdAt;

    public CourseDto() {}

    public static CourseDto fromEntity(Course c) {
        if (c == null) return null;
        CourseDto d = new CourseDto();
        d.id = c.getId();
        d.courseCode = c.getCourseCode();
        d.courseName = c.getCourseName();
        d.description = c.getDescription();
        d.classId = c.getClassId();
        d.schoolId = c.getSchoolId();
        d.teacherId = c.getTeacherId();
        d.subject = c.getSubject();
        d.grade = c.getGrade();
        d.active = c.isActive();
        d.createdAt = c.getCreatedAt();
        return d;
    }

    public static List<CourseDto> fromEntities(List<Course> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(CourseDto::fromEntity).collect(Collectors.toList());
    }

    public Course toEntity() {
        Course c = new Course();
        c.setId(this.id);
        c.setCourseCode(this.courseCode);
        c.setCourseName(this.courseName);
        c.setDescription(this.description);
        c.setClassId(this.classId);
        c.setSchoolId(this.schoolId);
        c.setTeacherId(this.teacherId);
        c.setSubject(this.subject);
        c.setGrade(this.grade);
        c.setActive(this.active);
        c.setCreatedAt(this.createdAt);
        return c;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
