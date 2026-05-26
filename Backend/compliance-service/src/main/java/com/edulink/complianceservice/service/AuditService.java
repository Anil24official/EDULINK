package com.edulink.complianceservice.service;

import com.edulink.complianceservice.dto.AuditDto;
import com.edulink.complianceservice.entity.Audit;
import com.edulink.complianceservice.exception.ResourceNotFoundException;
import com.edulink.complianceservice.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    @Autowired
    private AuditRepository auditRepository;

    public AuditDto create(AuditDto dto) {
        Audit a = new Audit();
        a.setScope(dto.getScope());
        a.setFindings(dto.getFindings());
        a.setAuditDate(dto.getAuditDate());
        a.setStatus(dto.getStatus());
        a.setSchoolId(dto.getSchoolId());

        String email = currentUserEmail();
        a.setOfficerEmail(email != null ? email : dto.getOfficerEmail());

        return toDto(auditRepository.save(a));
    }

    public List<AuditDto> getAll() {
        return auditRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AuditDto> filter(String status, String schoolId, String officerEmail) {
        List<Audit> rows;
        if (status != null && !status.isBlank()) {
            rows = auditRepository.findByStatus(status);
        } else if (schoolId != null && !schoolId.isBlank()) {
            rows = auditRepository.findBySchoolId(schoolId);
        } else if (officerEmail != null && !officerEmail.isBlank()) {
            rows = auditRepository.findByOfficerEmail(officerEmail);
        } else {
            rows = auditRepository.findAll();
        }
        return rows.stream().map(this::toDto).collect(Collectors.toList());
    }

    public AuditDto getById(Long id) {
        Audit a = auditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit not found with id " + id));
        return toDto(a);
    }

    public AuditDto update(Long id, AuditDto dto) {
        Audit a = auditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit not found with id " + id));
        if (dto.getScope() != null) a.setScope(dto.getScope());
        if (dto.getFindings() != null) a.setFindings(dto.getFindings());
        if (dto.getAuditDate() != null) a.setAuditDate(dto.getAuditDate());
        if (dto.getStatus() != null) a.setStatus(dto.getStatus());
        if (dto.getSchoolId() != null) a.setSchoolId(dto.getSchoolId());
        return toDto(auditRepository.save(a));
    }

    public AuditDto updateStatus(Long id, String status) {
        Audit a = auditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit not found with id " + id));
        a.setStatus(status);
        return toDto(auditRepository.save(a));
    }

    public void delete(Long id) {
        if (!auditRepository.existsById(id)) {
            throw new ResourceNotFoundException("Audit not found with id " + id);
        }
        auditRepository.deleteById(id);
    }

    private AuditDto toDto(Audit a) {
        AuditDto d = new AuditDto();
        d.setId(a.getId());
        d.setOfficerEmail(a.getOfficerEmail());
        d.setScope(a.getScope());
        d.setFindings(a.getFindings());
        d.setAuditDate(a.getAuditDate());
        d.setStatus(a.getStatus());
        d.setSchoolId(a.getSchoolId());
        d.setCreatedAt(a.getCreatedAt());
        d.setUpdatedAt(a.getUpdatedAt());
        return d;
    }

    private String currentUserEmail() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getName();
            return principal == null ? null : principal.toString();
        } catch (Exception ex) {
            return null;
        }
    }
}
