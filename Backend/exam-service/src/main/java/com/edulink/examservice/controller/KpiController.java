package com.edulink.examservice.controller;

import com.edulink.examservice.dto.ApiResponse;
import com.edulink.examservice.dto.KpiDto;
import com.edulink.examservice.entity.Grade;
import com.edulink.examservice.repository.ExamRepository;
import com.edulink.examservice.repository.GradeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam/kpis")
public class KpiController {

    private final GradeRepository gradeRepository;
    private final ExamRepository examRepository;

    public KpiController(GradeRepository gradeRepository, ExamRepository examRepository) {
        this.gradeRepository = gradeRepository;
        this.examRepository = examRepository;
    }

    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','EDUCATION_BOARD_OFFICER','REGULATOR','COMPLIANCE_OFFICER','TEACHER')")
    @GetMapping
    public ResponseEntity<ApiResponse<KpiDto>> kpis() {
        List<Grade> grades = gradeRepository.findAll();
        long total = grades.size();
        long passing = grades.stream()
                .filter(g -> g.getMarksObtained() >= g.getPassingMarks())
                .count();
        long failing = total - passing;
        double passRate = total == 0 ? 0.0 : (double) passing * 100.0 / (double) total;

        KpiDto dto = new KpiDto();
        dto.setTotalGrades(total);
        dto.setPassingGrades(passing);
        dto.setFailingGrades(failing);
        dto.setPassRate(round2(passRate));
        dto.setTotalExams(examRepository.count());
        return ResponseEntity.ok(ApiResponse.success("Exam KPIs", dto));
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
