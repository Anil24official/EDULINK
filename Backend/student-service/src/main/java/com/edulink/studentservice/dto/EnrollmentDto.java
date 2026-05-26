package com.edulink.studentservice.dto;

import com.edulink.studentservice.entity.Enrollment;
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
public class EnrollmentDto {
    private Long id;
    private Long studentId;
    private String studentEmail;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String status;
    private LocalDateTime enrolledAt;

    public static EnrollmentDto fromEntity(Enrollment e) {
        if (e == null) return null;
        return EnrollmentDto.builder()
                .id(e.getId())
                .studentId(e.getStudentId())
                .studentEmail(e.getStudentEmail())
                .courseId(e.getCourseId())
                .courseName(e.getCourseName())
                .courseCode(e.getCourseCode())
                .status(e.getStatus())
                .enrolledAt(e.getEnrolledAt())
                .build();
    }

    public static List<EnrollmentDto> fromEntities(List<Enrollment> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(EnrollmentDto::fromEntity).collect(Collectors.toList());
    }
}
