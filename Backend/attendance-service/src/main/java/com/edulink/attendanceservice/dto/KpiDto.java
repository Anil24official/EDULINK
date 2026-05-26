package com.edulink.attendanceservice.dto;

public class KpiDto {
    private long totalRecords;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private long excusedCount;
    private double attendancePercentage;
    private String schoolId;

    public KpiDto() {}

    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }
    public long getPresentCount() { return presentCount; }
    public void setPresentCount(long presentCount) { this.presentCount = presentCount; }
    public long getAbsentCount() { return absentCount; }
    public void setAbsentCount(long absentCount) { this.absentCount = absentCount; }
    public long getLateCount() { return lateCount; }
    public void setLateCount(long lateCount) { this.lateCount = lateCount; }
    public long getExcusedCount() { return excusedCount; }
    public void setExcusedCount(long excusedCount) { this.excusedCount = excusedCount; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
}
