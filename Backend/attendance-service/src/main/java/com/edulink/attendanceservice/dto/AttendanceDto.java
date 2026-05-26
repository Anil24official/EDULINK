package com.edulink.attendanceservice.dto;

import com.edulink.attendanceservice.entity.Attendance;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceDto {

    private Long id;

    @NotBlank(message = "Roll number is required")
    @Pattern(regexp = "^[A-Z]{3}\\d{6}$",
            message = "Roll number must follow pattern: 3 uppercase letters followed by 6 digits (e.g., SCH001101)")
    @Size(max = 20)
    private String rollNumber;

    @NotNull(message = "Course ID is required")
    @Positive
    private Long courseId;

    @NotBlank(message = "School ID is required")
    @Size(max = 20)
    private String schoolId;

    @NotNull(message = "Attendance date is required")
    @PastOrPresent
    private LocalDate attendanceDate;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(PRESENT|ABSENT|LATE|EXCUSED)$",
            message = "Status must be one of: PRESENT, ABSENT, LATE, EXCUSED")
    private String status;

    @Size(max = 50)
    private String markedBy;

    private LocalDateTime createdAt;

    public AttendanceDto() {}

    public static AttendanceDto fromEntity(Attendance e) {
        if (e == null) return null;
        AttendanceDto d = new AttendanceDto();
        d.id = e.getId();
        d.rollNumber = e.getRollNumber();
        d.courseId = e.getCourseId();
        d.schoolId = e.getSchoolId();
        d.attendanceDate = e.getAttendanceDate();
        d.status = e.getStatus();
        d.markedBy = e.getMarkedBy();
        d.createdAt = e.getCreatedAt();
        return d;
    }

    public static List<AttendanceDto> fromEntities(List<Attendance> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(AttendanceDto::fromEntity).collect(Collectors.toList());
    }

    public Attendance toEntity() {
        Attendance a = new Attendance();
        a.setId(this.id);
        a.setRollNumber(this.rollNumber);
        a.setCourseId(this.courseId);
        a.setSchoolId(this.schoolId);
        a.setAttendanceDate(this.attendanceDate);
        a.setStatus(this.status);
        a.setMarkedBy(this.markedBy);
        a.setCreatedAt(this.createdAt);
        return a;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMarkedBy() { return markedBy; }
    public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
