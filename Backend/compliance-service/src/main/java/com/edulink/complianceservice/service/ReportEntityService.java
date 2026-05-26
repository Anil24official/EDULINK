package com.edulink.complianceservice.service;

import com.edulink.complianceservice.dto.ReportEntityDto;
import com.edulink.complianceservice.entity.Report;
import com.edulink.complianceservice.entity.Rule;
import com.edulink.complianceservice.exception.ResourceNotFoundException;
import com.edulink.complianceservice.repository.ReportRepository;
import com.edulink.complianceservice.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportEntityService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private RuleRepository ruleRepository;

    public ReportEntityDto save(ReportEntityDto dto) {
        Report r = new Report();
        r.setScope(dto.getScope());
        r.setSchoolId(dto.getSchoolId());
        r.setTitle(dto.getTitle());
        r.setDescription(dto.getDescription());
        r.setMetrics(dto.getMetrics());
        r.setStatus(dto.getStatus());
        String me = currentUserEmail();
        r.setGeneratedByEmail(me != null ? me : dto.getGeneratedByEmail());
        return toDto(reportRepository.save(r));
    }

    public List<ReportEntityDto> list(String scope, String schoolId) {
        List<Report> rows;
        if (scope != null && !scope.isBlank()) {
            rows = reportRepository.findByScopeOrderByGeneratedDateDesc(scope);
        } else if (schoolId != null && !schoolId.isBlank()) {
            rows = reportRepository.findBySchoolIdOrderByGeneratedDateDesc(schoolId);
        } else {
            rows = reportRepository.findAllByOrderByGeneratedDateDesc();
        }
        return rows.stream().map(this::toDto).collect(Collectors.toList());
    }

    public ReportEntityDto getById(Long id) {
        Report r = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + id));
        return toDto(r);
    }

    public ReportEntityDto update(Long id, ReportEntityDto dto) {
        Report r = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + id));
        if (dto.getScope() != null) r.setScope(dto.getScope());
        if (dto.getSchoolId() != null) r.setSchoolId(dto.getSchoolId());
        if (dto.getTitle() != null) r.setTitle(dto.getTitle());
        if (dto.getDescription() != null) r.setDescription(dto.getDescription());
        if (dto.getMetrics() != null) r.setMetrics(dto.getMetrics());
        if (dto.getStatus() != null) r.setStatus(dto.getStatus());
        return toDto(reportRepository.save(r));
    }

    public void delete(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Report not found: " + id);
        }
        reportRepository.deleteById(id);
    }

    /**
     * Build a fresh report snapshot for the given scope by aggregating data the
     * compliance-service can see locally. For Compliance/Rule scopes we use the
     * Rule table; for others the caller can supply an explicit metrics payload.
     */
    public ReportEntityDto generate(String scope, String title, String schoolId, String description) {
        Map<String, Object> metricsMap = new HashMap<>();

        if (scope == null || scope.isBlank()) {
            scope = "Compliance";
        }
        if ("Compliance".equalsIgnoreCase(scope) || "Rule".equalsIgnoreCase(scope)) {
            List<Rule> rules = ruleRepository.findAll();
            int live = 0, pending = 0, accepted = 0, rejected = 0, review = 0;
            for (Rule r : rules) {
                if (r.isActive()) live++;
                String s = r.getStatus() == null ? "" : r.getStatus().toLowerCase();
                switch (s) {
                    case "pending":  pending++;  break;
                    case "approved": accepted++; break;
                    case "rejected": rejected++; break;
                    case "review":   review++;   break;
                    default: break;
                }
            }
            metricsMap.put("totalRules", rules.size());
            metricsMap.put("liveRules", live);
            metricsMap.put("pendingRules", pending);
            metricsMap.put("acceptedRules", accepted);
            metricsMap.put("rejectedRules", rejected);
            metricsMap.put("reviewRules", review);
        } else {
            metricsMap.put("note", "Snapshot empty — metrics body should be supplied for non-compliance scopes");
        }

        ReportEntityDto dto = new ReportEntityDto();
        dto.setScope(scope);
        dto.setTitle(title != null && !title.isBlank() ? title : (scope + " Report"));
        dto.setSchoolId(schoolId);
        dto.setDescription(description);
        dto.setMetrics(mapToCompactJson(metricsMap));
        return save(dto);
    }

    private ReportEntityDto toDto(Report r) {
        ReportEntityDto d = new ReportEntityDto();
        d.setId(r.getId());
        d.setScope(r.getScope());
        d.setSchoolId(r.getSchoolId());
        d.setTitle(r.getTitle());
        d.setDescription(r.getDescription());
        d.setMetrics(r.getMetrics());
        d.setGeneratedByEmail(r.getGeneratedByEmail());
        d.setGeneratedDate(r.getGeneratedDate());
        d.setStatus(r.getStatus());
        return d;
    }

    private String mapToCompactJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(e.getKey()).append("\":");
            Object v = e.getValue();
            if (v instanceof Number || v instanceof Boolean) {
                sb.append(v);
            } else {
                String s = v == null ? "" : v.toString().replace("\"", "\\\"");
                sb.append("\"").append(s).append("\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String currentUserEmail() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getName();
            return principal != null ? principal.toString() : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
