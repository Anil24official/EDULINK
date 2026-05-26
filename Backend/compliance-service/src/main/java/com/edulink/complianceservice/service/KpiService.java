package com.edulink.complianceservice.service;

import com.edulink.complianceservice.client.AttendanceKpiClient;
import com.edulink.complianceservice.client.ExamKpiClient;
import com.edulink.complianceservice.dto.ApiResponse;
import com.edulink.complianceservice.dto.AttendanceKpiDto;
import com.edulink.complianceservice.dto.ExamKpiDto;
import com.edulink.complianceservice.dto.KpiDto;
import com.edulink.complianceservice.entity.Audit;
import com.edulink.complianceservice.entity.ComplianceRecord;
import com.edulink.complianceservice.entity.Rule;
import com.edulink.complianceservice.repository.AuditRepository;
import com.edulink.complianceservice.repository.ComplianceRecordRepository;
import com.edulink.complianceservice.repository.RuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KpiService {

    private static final Logger log = LoggerFactory.getLogger(KpiService.class);

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private ComplianceRecordRepository complianceRecordRepository;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private ExamKpiClient examKpiClient;

    @Autowired
    private AttendanceKpiClient attendanceKpiClient;

    /** Compliance adherence rate computed locally from ComplianceRecord results. */
    public KpiDto getComplianceKpis(String schoolId) {
        KpiDto dto = new KpiDto();
        dto.setSchoolId(schoolId);
        populateComplianceMetrics(dto, schoolId);
        return dto;
    }

    /** All KPIs aggregated across exam, attendance, and compliance services. */
    public KpiDto getAggregatedKpis(String schoolId) {
        KpiDto dto = new KpiDto();
        dto.setSchoolId(schoolId);

        // Exam pass rate (via Feign)
        try {
            ResponseEntity<ApiResponse<ExamKpiDto>> resp = examKpiClient.kpis();
            ExamKpiDto exam = resp.getBody() != null ? resp.getBody().getData() : null;
            if (exam != null) {
                dto.setExamPassRate(exam.getPassRate());
                dto.setTotalGrades(exam.getTotalGrades());
                dto.setPassingGrades(exam.getPassingGrades());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch exam KPIs: {}", e.getMessage());
        }

        // Attendance % (via Feign)
        try {
            ResponseEntity<ApiResponse<AttendanceKpiDto>> resp = attendanceKpiClient.kpis(schoolId);
            AttendanceKpiDto att = resp.getBody() != null ? resp.getBody().getData() : null;
            if (att != null) {
                dto.setAttendancePercentage(att.getAttendancePercentage());
                dto.setTotalAttendanceRecords(att.getTotalRecords());
                dto.setPresentCount(att.getPresentCount());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch attendance KPIs: {}", e.getMessage());
        }

        // Compliance adherence + rule/audit metrics (local)
        populateComplianceMetrics(dto, schoolId);

        return dto;
    }

    private void populateComplianceMetrics(KpiDto dto, String schoolId) {
        // Compliance records
        List<ComplianceRecord> records = (schoolId != null && !schoolId.isBlank())
                ? complianceRecordRepository.findBySchoolId(schoolId)
                : complianceRecordRepository.findAll();
        long totalRecords = records.size();
        long passing = records.stream()
                .filter(r -> "PASS".equalsIgnoreCase(r.getResult()))
                .count();
        dto.setTotalComplianceRecords(totalRecords);
        dto.setPassingComplianceRecords(passing);
        dto.setComplianceAdherenceRate(totalRecords == 0
                ? 0.0 : round2((double) passing * 100.0 / (double) totalRecords));

        // Rules (rule activation rate)
        List<Rule> rules = ruleRepository.findAll();
        long totalRules = rules.size();
        long activeRules = rules.stream()
                .filter(r -> r.isActive() && "approved".equalsIgnoreCase(r.getStatus()))
                .count();
        dto.setTotalRules(totalRules);
        dto.setActiveRules(activeRules);
        dto.setRuleActivationRate(totalRules == 0
                ? 0.0 : round2((double) activeRules * 100.0 / (double) totalRules));

        // Audits (closure rate)
        List<Audit> audits = (schoolId != null && !schoolId.isBlank())
                ? auditRepository.findBySchoolId(schoolId)
                : auditRepository.findAll();
        long totalAudits = audits.size();
        long closedAudits = audits.stream()
                .filter(a -> {
                    String s = a.getStatus();
                    return s != null && (s.equalsIgnoreCase("RESOLVED") || s.equalsIgnoreCase("CLOSED"));
                })
                .count();
        dto.setTotalAudits(totalAudits);
        dto.setClosedAudits(closedAudits);
        dto.setAuditClosureRate(totalAudits == 0
                ? 0.0 : round2((double) closedAudits * 100.0 / (double) totalAudits));
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
