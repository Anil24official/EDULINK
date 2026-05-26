package com.edulink.complianceservice.service;

import com.edulink.complianceservice.dto.ComplianceRecordDto;
import com.edulink.complianceservice.entity.ComplianceRecord;
import com.edulink.complianceservice.exception.ResourceNotFoundException;
import com.edulink.complianceservice.repository.ComplianceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplianceRecordService {

    @Autowired
    private ComplianceRecordRepository complianceRecordRepository;

    public ComplianceRecordDto create(ComplianceRecordDto dto) {
        ComplianceRecord entity = new ComplianceRecord();
        entity.setEntityId(dto.getEntityId());
        entity.setType(dto.getType());
        entity.setResult(dto.getResult());
        entity.setNotes(dto.getNotes());
        entity.setSchoolId(dto.getSchoolId());
        entity.setRecordedDate(dto.getRecordedDate());

        String email = currentUserEmail();
        entity.setRecordedByEmail(email != null ? email : dto.getRecordedByEmail());

        return toDto(complianceRecordRepository.save(entity));
    }

    public List<ComplianceRecordDto> getAll() {
        return complianceRecordRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ComplianceRecordDto> filter(String type, String schoolId, String entityId) {
        List<ComplianceRecord> records;
        if (type != null && !type.isBlank()) {
            records = complianceRecordRepository.findByType(type);
        } else if (schoolId != null && !schoolId.isBlank()) {
            records = complianceRecordRepository.findBySchoolId(schoolId);
        } else if (entityId != null && !entityId.isBlank()) {
            records = complianceRecordRepository.findByEntityId(entityId);
        } else {
            records = complianceRecordRepository.findAll();
        }
        return records.stream().map(this::toDto).collect(Collectors.toList());
    }

    public ComplianceRecordDto getById(Long id) {
        ComplianceRecord record = complianceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance record not found with id " + id));
        return toDto(record);
    }

    public ComplianceRecordDto update(Long id, ComplianceRecordDto dto) {
        ComplianceRecord record = complianceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance record not found with id " + id));
        if (dto.getEntityId() != null) record.setEntityId(dto.getEntityId());
        if (dto.getType() != null) record.setType(dto.getType());
        if (dto.getResult() != null) record.setResult(dto.getResult());
        if (dto.getNotes() != null) record.setNotes(dto.getNotes());
        if (dto.getSchoolId() != null) record.setSchoolId(dto.getSchoolId());
        if (dto.getRecordedDate() != null) record.setRecordedDate(dto.getRecordedDate());
        return toDto(complianceRecordRepository.save(record));
    }

    public void delete(Long id) {
        if (!complianceRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Compliance record not found with id " + id);
        }
        complianceRecordRepository.deleteById(id);
    }

    private ComplianceRecordDto toDto(ComplianceRecord e) {
        ComplianceRecordDto d = new ComplianceRecordDto();
        d.setId(e.getId());
        d.setEntityId(e.getEntityId());
        d.setType(e.getType());
        d.setResult(e.getResult());
        d.setNotes(e.getNotes());
        d.setRecordedByEmail(e.getRecordedByEmail());
        d.setSchoolId(e.getSchoolId());
        d.setRecordedDate(e.getRecordedDate());
        d.setCreatedAt(e.getCreatedAt());
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
