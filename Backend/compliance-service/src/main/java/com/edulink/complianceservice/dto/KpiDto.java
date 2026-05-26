package com.edulink.complianceservice.dto;

public class KpiDto {

    /** Exam pass rate (%) — share of grades where marksObtained >= passingMarks. */
    private double examPassRate;
    private long totalGrades;
    private long passingGrades;

    /** Attendance percentage — share of records marked PRESENT or LATE. */
    private double attendancePercentage;
    private long totalAttendanceRecords;
    private long presentCount;

    /** Compliance adherence rate (%) — share of compliance records with result=PASS. */
    private double complianceAdherenceRate;
    private long totalComplianceRecords;
    private long passingComplianceRecords;

    /** Rule activation rate (%) — share of rules that are approved AND active. */
    private double ruleActivationRate;
    private long totalRules;
    private long activeRules;

    /** Audit closure rate (%) — share of audits with status RESOLVED or CLOSED. */
    private double auditClosureRate;
    private long totalAudits;
    private long closedAudits;

    private String schoolId;

    public KpiDto() {}

    public double getExamPassRate() { return examPassRate; }
    public void setExamPassRate(double examPassRate) { this.examPassRate = examPassRate; }
    public long getTotalGrades() { return totalGrades; }
    public void setTotalGrades(long totalGrades) { this.totalGrades = totalGrades; }
    public long getPassingGrades() { return passingGrades; }
    public void setPassingGrades(long passingGrades) { this.passingGrades = passingGrades; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    public long getTotalAttendanceRecords() { return totalAttendanceRecords; }
    public void setTotalAttendanceRecords(long totalAttendanceRecords) { this.totalAttendanceRecords = totalAttendanceRecords; }
    public long getPresentCount() { return presentCount; }
    public void setPresentCount(long presentCount) { this.presentCount = presentCount; }

    public double getComplianceAdherenceRate() { return complianceAdherenceRate; }
    public void setComplianceAdherenceRate(double complianceAdherenceRate) { this.complianceAdherenceRate = complianceAdherenceRate; }
    public long getTotalComplianceRecords() { return totalComplianceRecords; }
    public void setTotalComplianceRecords(long totalComplianceRecords) { this.totalComplianceRecords = totalComplianceRecords; }
    public long getPassingComplianceRecords() { return passingComplianceRecords; }
    public void setPassingComplianceRecords(long passingComplianceRecords) { this.passingComplianceRecords = passingComplianceRecords; }

    public double getRuleActivationRate() { return ruleActivationRate; }
    public void setRuleActivationRate(double ruleActivationRate) { this.ruleActivationRate = ruleActivationRate; }
    public long getTotalRules() { return totalRules; }
    public void setTotalRules(long totalRules) { this.totalRules = totalRules; }
    public long getActiveRules() { return activeRules; }
    public void setActiveRules(long activeRules) { this.activeRules = activeRules; }

    public double getAuditClosureRate() { return auditClosureRate; }
    public void setAuditClosureRate(double auditClosureRate) { this.auditClosureRate = auditClosureRate; }
    public long getTotalAudits() { return totalAudits; }
    public void setTotalAudits(long totalAudits) { this.totalAudits = totalAudits; }
    public long getClosedAudits() { return closedAudits; }
    public void setClosedAudits(long closedAudits) { this.closedAudits = closedAudits; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
}
