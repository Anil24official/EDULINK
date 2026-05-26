package com.edulink.attendanceservice.controller;
import com.edulink.attendanceservice.dto.ApiResponse;
import com.edulink.attendanceservice.dto.AttendanceDto;
import com.edulink.attendanceservice.entity.Attendance;
import com.edulink.attendanceservice.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/teacher/mark-attendance")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<AttendanceDto>> markAttendance(@Valid @RequestBody AttendanceDto request) {
        String teacherEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Attendance attendance = request.toEntity();
        attendance.setMarkedBy(teacherEmail);
        Attendance saved = attendanceService.markAttendance(attendance);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Attendance marked", AttendanceDto.fromEntity(saved)));
    }

    @PostMapping("/teacher/mark-attendance/bulk")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> markAttendanceBulk(
            @RequestBody List<AttendanceDto> records) {
        String teacherEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Attendance> toSave = records.stream()
                .map(AttendanceDto::toEntity)
                .peek(a -> a.setMarkedBy(teacherEmail))
                .collect(Collectors.toList());
        List<Attendance> saved = attendanceService.markAttendanceBulk(toSave);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Bulk attendance marked for " + saved.size() + " students",
                    AttendanceDto.fromEntities(saved)));
    }

    @GetMapping("/student/attendance")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getStudentAttendance(
            @RequestParam(required = false) String rollNumber) {
        List<Attendance> rows;
        if (rollNumber == null || rollNumber.isBlank()) {
            Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
            if (details instanceof String && !((String) details).contains("@")) {
                rollNumber = (String) details;
                rows = attendanceService.getAttendanceByRollNumber(rollNumber);
            } else {
                rows = attendanceService.getAttendanceByEmail(
                        SecurityContextHolder.getContext().getAuthentication().getName());
            }
        } else {
            rows = attendanceService.getAttendanceByRollNumber(rollNumber);
        }
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved",
                AttendanceDto.fromEntities(rows)));
    }

    @GetMapping("/admin/attendance-report")
    @PreAuthorize("hasRole('SCHOOL_ADMIN') or hasRole('TEACHER') or hasRole('EDUCATION_BOARD_OFFICER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getAttendanceReport(@RequestParam String schoolId) {
        return ResponseEntity.ok(ApiResponse.success("Attendance report",
                AttendanceDto.fromEntities(attendanceService.getAttendanceReport(schoolId))));
    }

    @GetMapping("/teacher/attendance-progress")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAttendanceProgress(
            @RequestParam Long courseId) {
        return ResponseEntity.ok(ApiResponse.success("Attendance progress",
                attendanceService.getProgressByCourse(courseId)));
    }

    @GetMapping("/teacher/low-attendance")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLowAttendance(
            @RequestParam Long courseId) {
        return ResponseEntity.ok(ApiResponse.success("Low attendance students",
                attendanceService.getLowAttendanceStudents(courseId)));
    }
}
