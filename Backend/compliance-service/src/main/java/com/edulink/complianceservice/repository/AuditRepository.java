package com.edulink.complianceservice.repository;

import com.edulink.complianceservice.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    List<Audit> findByOfficerEmail(String officerEmail);

    List<Audit> findByStatus(String status);

    List<Audit> findBySchoolId(String schoolId);
}
