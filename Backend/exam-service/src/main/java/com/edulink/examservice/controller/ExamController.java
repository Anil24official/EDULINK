package com.edulink.examservice.controller;

import com.edulink.examservice.dto.ApiResponse;
import com.edulink.examservice.dto.CreateGradeRequest;
import com.edulink.examservice.dto.ExamDto;
import com.edulink.examservice.dto.ExamSubmissionDto;
import com.edulink.examservice.dto.GradeDto;
import com.edulink.examservice.dto.SubmitExamRequest;
import com.edulink.examservice.entity.Exam;
import com.edulink.examservice.entity.ExamSubmission;
import com.edulink.examservice.entity.Grade;
import com.edulink.examservice.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController {
    private final ExamService examService;
    private final GradeService gradeService;
    private final ExamSubmissionService examSubmissionService;

    public ExamController(ExamService examService, GradeService gradeService, ExamSubmissionService examSubmissionService) {
        this.examService = examService;
        this.gradeService = gradeService;
        this.examSubmissionService = examSubmissionService;
    }

    @PostMapping(value = "/teacher/create-exam", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<ExamDto>> createExam(
            @RequestParam String courseCode,
            @RequestParam String examTitle,
            @RequestParam String examType,
            @RequestParam int totalMarks,
            @RequestParam int passingMarks,
            @RequestParam String schoolId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime examDate,
            @RequestParam(required = false) Integer durationMinutes,
            @RequestParam(required = false) MultipartFile questionsFile) throws IOException {

        Exam exam = new Exam();
        exam.setCourseCode(courseCode);
        exam.setExamTitle(examTitle);
        exam.setExamType(examType);
        exam.setTotalMarks(totalMarks);
        exam.setPassingMarks(passingMarks);
        exam.setSchoolId(schoolId);
        exam.setExamDate(examDate);
        exam.setDurationMinutes(durationMinutes);

        Exam saved = examService.createExam(exam, questionsFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam created", ExamDto.fromEntity(saved)));
    }

    @PostMapping("/teacher/grade-student")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<GradeDto>> gradeStudent(@Valid @RequestBody CreateGradeRequest request) {
        Grade grade = new Grade();
        grade.setCourseCode(request.getCourseCode());
        grade.setExamType(request.getExamType());
        grade.setRollNumber(request.getRollNumber());
        grade.setStudentEmail(request.getStudentEmail());
        grade.setMarksObtained(request.getMarksObtained());
        grade.setTotalMarks(request.getTotalMarks());
        grade.setPassingMarks(request.getPassingMarks());
        grade.setRemarks(request.getRemarks());

        Grade saved = gradeService.gradeStudent(grade);
        return ResponseEntity.ok(ApiResponse.success("Student graded", GradeDto.fromEntity(saved)));
    }

    @GetMapping("/student/grades")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<GradeDto>>> getStudentGrades() {
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        String rollNumber = (details instanceof String d && !d.contains("@")) ? d : null;
        if (rollNumber == null) {
            throw new RuntimeException("Roll number not found in token");
        }
        return ResponseEntity.ok(ApiResponse.success("Grades retrieved",
                GradeDto.fromEntities(gradeService.getGradesByRollNumber(rollNumber))));
    }

    @GetMapping("/student/exams")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ExamDto>>> getAvailableExams(@RequestParam List<String> courseCodes) {
        List<Exam> exams = examService.getExamsByCourseCodes(courseCodes);
        return ResponseEntity.ok(ApiResponse.success("Available exams retrieved",
                ExamDto.fromEntities(exams)));
    }

    @GetMapping("/student/download-exam-questions/{examId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<byte[]> downloadExamQuestions(@PathVariable Long examId) throws IOException {
        byte[] fileContent = examService.downloadExamQuestions(examId);
        String fileName = examService.getExamQuestionsFileName(examId);
        String contentType = examService.getExamQuestionsContentType(examId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(fileContent);
    }

    @PostMapping("/student/start-exam")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ExamSubmissionDto>> startExam(@RequestBody SubmitExamRequest request) {
        String studentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        String rollNumber = (details instanceof String s && !s.contains("@")) ? s : null;

        ExamSubmission attempt = examSubmissionService.startExam(
                request.getCourseCode(), request.getExamType(), studentEmail, rollNumber);
        return ResponseEntity.ok(ApiResponse.success("Exam started", ExamSubmissionDto.fromEntity(attempt)));
    }

    @PostMapping("/student/submit-exam")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ExamSubmissionDto>> submitExam(@Valid @RequestBody SubmitExamRequest request) {
        String studentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        String rollNumber = (details instanceof String s && !s.contains("@")) ? s : null;

        ExamSubmission submission = examSubmissionService.submitExam(
                request.getCourseCode(), request.getExamType(),
                studentEmail, rollNumber, request.getSubmissionContent());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam submitted successfully", ExamSubmissionDto.fromEntity(submission)));
    }

    @GetMapping("/teacher/exam-submissions/{courseCode}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ExamSubmissionDto>>> getExamSubmissions(@PathVariable String courseCode) {
        List<ExamSubmission> submissions = examSubmissionService.getSubmissionsByCourseCode(courseCode);
        return ResponseEntity.ok(ApiResponse.success("Exam submissions retrieved",
                ExamSubmissionDto.fromEntities(submissions)));
    }

    @GetMapping("/teacher/submission/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<ExamSubmissionDto>> getSubmissionById(@PathVariable Long id) {
        ExamSubmission submission = examSubmissionService.getSubmissionById(id)
                .orElseThrow(() -> new com.edulink.examservice.exception.ResourceNotFoundException(
                        "Submission not found: " + id));
        return ResponseEntity.ok(ApiResponse.success("Submission retrieved",
                ExamSubmissionDto.fromEntity(submission)));
    }

    @GetMapping("/teacher/grades")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<GradeDto>>> getGradesByExam(
            @RequestParam String courseCode,
            @RequestParam String examType) {
        List<Grade> grades = gradeService.getGradesByCourseCodeAndExamType(courseCode, examType);
        return ResponseEntity.ok(ApiResponse.success("Grades retrieved",
                GradeDto.fromEntities(grades)));
    }

    @GetMapping("/teacher/grades-by-course/{courseCode}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<GradeDto>>> getGradesByCourse(@PathVariable String courseCode) {
        return ResponseEntity.ok(ApiResponse.success("Grades retrieved",
                GradeDto.fromEntities(gradeService.getGradesByCourseCode(courseCode))));
    }

    @DeleteMapping("/teacher/reset-attempt")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Long>> resetAttempt(
            @RequestParam String courseCode,
            @RequestParam String examType,
            @RequestParam String rollNumber) {
        long deleted = examSubmissionService.resetAttempt(courseCode, examType, rollNumber);
        return ResponseEntity.ok(ApiResponse.success(
                deleted == 0 ? "Nothing to reset" : "Reset complete — " + deleted + " row(s) removed",
                deleted));
    }

    @GetMapping("/teacher/exams/{courseCode}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ExamDto>>> getExamsByCourseCode(@PathVariable String courseCode) {
        List<Exam> exams = examService.getExamsByCourseCode(courseCode);
        return ResponseEntity.ok(ApiResponse.success("Exams retrieved", ExamDto.fromEntities(exams)));
    }

    @GetMapping("/teacher/exams")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ExamDto>>> getMyExams() {
        String teacherEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Exam> exams = examService.getExamsByTeacherEmail(teacherEmail);
        return ResponseEntity.ok(ApiResponse.success("Exams retrieved", ExamDto.fromEntities(exams)));
    }
}
