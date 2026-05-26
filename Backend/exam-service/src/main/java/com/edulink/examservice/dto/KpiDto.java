package com.edulink.examservice.dto;

public class KpiDto {
    private long totalGrades;
    private long passingGrades;
    private long failingGrades;
    private double passRate;
    private long totalExams;
    private String schoolId;

    public KpiDto() {}

    public long getTotalGrades() { return totalGrades; }
    public void setTotalGrades(long totalGrades) { this.totalGrades = totalGrades; }
    public long getPassingGrades() { return passingGrades; }
    public void setPassingGrades(long passingGrades) { this.passingGrades = passingGrades; }
    public long getFailingGrades() { return failingGrades; }
    public void setFailingGrades(long failingGrades) { this.failingGrades = failingGrades; }
    public double getPassRate() { return passRate; }
    public void setPassRate(double passRate) { this.passRate = passRate; }
    public long getTotalExams() { return totalExams; }
    public void setTotalExams(long totalExams) { this.totalExams = totalExams; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
}
