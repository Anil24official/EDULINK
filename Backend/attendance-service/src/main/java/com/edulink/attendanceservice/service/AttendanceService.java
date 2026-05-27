package com.edulink.attendanceservice.service;

import com.edulink.attendanceservice.entity.Attendance;
import com.edulink.attendanceservice.repository.AttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    private static final Logger audit = LoggerFactory.getLogger("AUDIT");
    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    /** Mark single attendance record */
    public Attendance markAttendance(Attendance attendance) {
        Attendance saved = attendanceRepository.save(attendance);
        audit.info("[AUDIT] action=ATTENDANCE_MARK actor={} roll={} course={} date={} status={}",
                saved.getMarkedBy(), saved.getRollNumber(), saved.getCourseId(),
                saved.getAttendanceDate(), saved.getStatus());
        return saved;
    }

    /** Bulk-save attendance records for all students in a class */
    public List<Attendance> markAttendanceBulk(List<Attendance> records) {
        List<Attendance> saved = attendanceRepository.saveAll(records);
        if (!saved.isEmpty()) {
            audit.info("[AUDIT] action=ATTENDANCE_BULK_MARK actor={} count={} course={} date={}",
                    saved.get(0).getMarkedBy(), saved.size(), saved.get(0).getCourseId(),
                    saved.get(0).getAttendanceDate());
        }
        return saved;
    }

    /** Get all attendance records for a student by rollNumber */
    public List<Attendance> getAttendanceByRollNumber(String rollNumber) {
        return attendanceRepository.findByRollNumber(rollNumber);
    }

    /** Get all attendance for a school (admin report) */
    public List<Attendance> getAttendanceReport(String schoolId) {
        return attendanceRepository.findBySchoolId(schoolId);
    }

    /** Fallback lookup by teacher email */
    public List<Attendance> getAttendanceByEmail(String email) {
        return attendanceRepository.findByMarkedBy(email);
    }

    /**
     * Build per-student progress summary for a given course/class.
     * Returns list of {rollNumber, present, absent, od, total, percentage}
     */
    public List<Map<String, Object>> getProgressByCourse(Long courseId) {
        List<Attendance> records = attendanceRepository.findByCourseId(courseId);
        // Group by rollNumber
        Map<String, List<Attendance>> grouped = records.stream()
            .collect(Collectors.groupingBy(Attendance::getRollNumber));

        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((rollNumber, recs) -> {
            long total   = recs.size();
            long present = recs.stream().filter(r -> "PRESENT".equals(r.getStatus())).count();
            long absent  = recs.stream().filter(r -> "ABSENT".equals(r.getStatus())).count();
            long od      = recs.stream().filter(r -> "OD".equals(r.getStatus())).count();
            double pct   = total > 0 ? ((present + od) * 100.0) / total : 0.0;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("rollNumber",  rollNumber);
            row.put("total",       total);
            row.put("present",     present);
            row.put("absent",      absent);
            row.put("od",          od);
            row.put("percentage",  Math.round(pct * 10.0) / 10.0);
            result.add(row);
        });
        return result;
    }

    /** Return only students with attendance < 50% in a course */
    public List<Map<String, Object>> getLowAttendanceStudents(Long courseId) {
        return getProgressByCourse(courseId).stream()
            .filter(s -> (double) s.get("percentage") < 50.0)
            .collect(Collectors.toList());
    }
}
