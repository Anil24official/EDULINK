package com.edulink.complianceservice.repository;

import com.edulink.complianceservice.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByScopeOrderByGeneratedDateDesc(String scope);

    List<Report> findBySchoolIdOrderByGeneratedDateDesc(String schoolId);

    List<Report> findAllByOrderByGeneratedDateDesc();
}
