package com.edulink.attendanceservice.controller;

import com.edulink.attendanceservice.dto.ApiResponse;
import com.edulink.attendanceservice.dto.KpiDto;
import com.edulink.attendanceservice.entity.Attendance;
import com.edulink.attendanceservice.repository.AttendanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance/kpis")
public class KpiController {

    private final AttendanceRepository attendanceRepository;

    public KpiController(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','EDUCATION_BOARD_OFFICER','REGULATOR','COMPLIANCE_OFFICER','TEACHER')")
    @GetMapping
    public ResponseEntity<ApiResponse<KpiDto>> kpis(@RequestParam(required = false) String schoolId) {
        List<Attendance> rows = (schoolId != null && !schoolId.isBlank())
                ? attendanceRepository.findBySchoolId(schoolId)
                : attendanceRepository.findAll();

        long total = rows.size();
        long present = rows.stream().filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus())).count();
        long absent = rows.stream().filter(a -> "ABSENT".equalsIgnoreCase(a.getStatus())).count();
        long late = rows.stream().filter(a -> "LATE".equalsIgnoreCase(a.getStatus())).count();
        long excused = rows.stream().filter(a -> "EXCUSED".equalsIgnoreCase(a.getStatus())).count();

        // Attendance % counts PRESENT + LATE as attended (LATE is still present, just late)
        long attended = present + late;
        double pct = total == 0 ? 0.0 : (double) attended * 100.0 / (double) total;

        KpiDto dto = new KpiDto();
        dto.setTotalRecords(total);
        dto.setPresentCount(present);
        dto.setAbsentCount(absent);
        dto.setLateCount(late);
        dto.setExcusedCount(excused);
        dto.setAttendancePercentage(round2(pct));
        dto.setSchoolId(schoolId);
        return ResponseEntity.ok(ApiResponse.success("Attendance KPIs", dto));
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
