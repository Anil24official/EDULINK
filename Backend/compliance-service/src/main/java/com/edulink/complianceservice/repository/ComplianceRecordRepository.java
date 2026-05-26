package com.edulink.complianceservice.repository;

import com.edulink.complianceservice.entity.ComplianceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {

    List<ComplianceRecord> findByType(String type);

    List<ComplianceRecord> findBySchoolId(String schoolId);

    List<ComplianceRecord> findByEntityId(String entityId);

    List<ComplianceRecord> findByRecordedByEmail(String recordedByEmail);
}
