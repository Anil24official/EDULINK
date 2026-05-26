package com.edulink.courseservice.dto;

import com.edulink.courseservice.entity.ClassRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ClassRoomDto {
    private Long id;
    private String className;
    private int grade;
    private String section;
    private String schoolId;
    private String teacherEmail;
    private int capacity;
    private LocalDateTime createdAt;

    public ClassRoomDto() {}

    public static ClassRoomDto fromEntity(ClassRoom c) {
        if (c == null) return null;
        ClassRoomDto d = new ClassRoomDto();
        d.id = c.getId();
        d.className = c.getClassName();
        d.grade = c.getGrade();
        d.section = c.getSection();
        d.schoolId = c.getSchoolId();
        d.teacherEmail = c.getTeacherEmail();
        d.capacity = c.getCapacity();
        d.createdAt = c.getCreatedAt();
        return d;
    }

    public static List<ClassRoomDto> fromEntities(List<ClassRoom> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(ClassRoomDto::fromEntity).collect(Collectors.toList());
    }

    public ClassRoom toEntity() {
        ClassRoom c = new ClassRoom();
        c.setId(this.id);
        c.setClassName(this.className);
        c.setGrade(this.grade);
        c.setSection(this.section);
        c.setSchoolId(this.schoolId);
        c.setTeacherEmail(this.teacherEmail);
        c.setCapacity(this.capacity);
        c.setCreatedAt(this.createdAt);
        return c;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public int getGrade() { return grade; }
    public void setGrade(int grade) { this.grade = grade; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public String getTeacherEmail() { return teacherEmail; }
    public void setTeacherEmail(String teacherEmail) { this.teacherEmail = teacherEmail; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
