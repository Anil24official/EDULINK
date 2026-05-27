package com.edulink.courseservice.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "classrooms")
public class ClassRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String className;
    private int grade;
    private String section;
    private String schoolId;
    private String teacherEmail;
    private int capacity;
    /** Spec §4.3 — Class.Schedule. Free-form text (e.g. "Mon-Fri 09:00-10:00"). */
    @Column(name = "schedule", length = 255)
    private String schedule;
    /** Spec §4.3 — Class.Status. Values: ACTIVE, INACTIVE, ARCHIVED. */
    @Column(name = "status", length = 20)
    private String status = "ACTIVE";
    private LocalDateTime createdAt;

    public ClassRoom() {
    }

    public ClassRoom(Long id, String className, int grade, String section, String schoolId,
                     String teacherEmail, int capacity, LocalDateTime createdAt) {
        this(id, className, grade, section, schoolId, teacherEmail, capacity, null, "ACTIVE", createdAt);
    }

    public ClassRoom(Long id, String className, int grade, String section, String schoolId,
                     String teacherEmail, int capacity, String schedule, String status, LocalDateTime createdAt) {
        this.id = id;
        this.className = className;
        this.grade = grade;
        this.section = section;
        this.schoolId = schoolId;
        this.teacherEmail = teacherEmail;
        this.capacity = capacity;
        this.schedule = schedule;
        this.status = status;
        this.createdAt = createdAt;
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
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null || status.isBlank()) status = "ACTIVE";
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String className;
        private int grade;
        private String section;
        private String schoolId;
        private String teacherEmail;
        private int capacity;
        private String schedule;
        private String status = "ACTIVE";
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder className(String className) { this.className = className; return this; }
        public Builder grade(int grade) { this.grade = grade; return this; }
        public Builder section(String section) { this.section = section; return this; }
        public Builder schoolId(String schoolId) { this.schoolId = schoolId; return this; }
        public Builder teacherEmail(String teacherEmail) { this.teacherEmail = teacherEmail; return this; }
        public Builder capacity(int capacity) { this.capacity = capacity; return this; }
        public Builder schedule(String schedule) { this.schedule = schedule; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ClassRoom build() {
            return new ClassRoom(id, className, grade, section, schoolId, teacherEmail, capacity, schedule, status, createdAt);
        }
    }
}
