package com.edulink.courseservice.controller;
import com.edulink.courseservice.dto.ApiResponse;
import com.edulink.courseservice.dto.ClassRoomDto;
import com.edulink.courseservice.dto.CourseDto;
import com.edulink.courseservice.entity.ClassRoom;
import com.edulink.courseservice.entity.Course;
import com.edulink.courseservice.service.CourseService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/course/admin")
@PreAuthorize("hasRole('SCHOOL_ADMIN')")
public class AdminCourseController {
    private final CourseService courseService;

    public AdminCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/create-course")
    public ResponseEntity<ApiResponse<CourseDto>> createCourse(@RequestBody CourseDto request) {
        Course saved = courseService.createCourse(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created", CourseDto.fromEntity(saved)));
    }

    @PostMapping("/create-class")
    public ResponseEntity<ApiResponse<ClassRoomDto>> createClass(@RequestBody ClassRoomDto request) {
        ClassRoom c = request.toEntity();

        // Auto-derive schoolId from JWT token
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof String schoolId) {
            c.setSchoolId(schoolId);
        }

        // Auto-generate classId as grade + section (e.g., grade=10, section=A → "10A")
        String classId = c.getGrade() + c.getSection();

        // Auto-generate className as "Class " + classId (e.g., "Class 10A")
        c.setClassName("Class " + classId);

        ClassRoom saved = courseService.createClass(c);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Class created", ClassRoomDto.fromEntity(saved)));
    }

    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<ClassRoomDto>>> getClasses() {
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof String schoolId) {
            return ResponseEntity.ok(ApiResponse.success("Classes retrieved",
                    ClassRoomDto.fromEntities(courseService.getClassesBySchool(schoolId))));
        }
        return ResponseEntity.ok(ApiResponse.success("Classes retrieved", List.of()));
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getCourses() {
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof String schoolId) {
            return ResponseEntity.ok(ApiResponse.success("Courses retrieved",
                    CourseDto.fromEntities(courseService.getCoursesBySchool(schoolId))));
        }
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved", List.of()));
    }

    @GetMapping("/attendance-report")
    public ResponseEntity<ApiResponse<Object>> attendanceReport() {
        return ResponseEntity.ok(ApiResponse.success("Attendance report (see attendance-service)", null));
    }
}
