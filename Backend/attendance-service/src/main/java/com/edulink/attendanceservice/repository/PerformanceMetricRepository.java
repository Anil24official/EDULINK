package com.edulink.attendanceservice.repository;

import com.edulink.attendanceservice.entity.PerformanceMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, Long> {

    List<PerformanceMetric> findByRollNumberOrderByMetricDateDesc(String rollNumber);

    List<PerformanceMetric> findByCourseIdOrderByMetricDateDesc(Long courseId);

    List<PerformanceMetric> findBySchoolIdOrderByMetricDateDesc(String schoolId);

    List<PerformanceMetric> findByRollNumberAndCourseIdOrderByMetricDateDesc(String rollNumber, Long courseId);
}
