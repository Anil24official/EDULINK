package com.edulink.complianceservice.entity;

/** Spec §4.7 — allowed values for ComplianceRecord.type. Persisted as String. */
public enum ComplianceType {
    COURSE, EXAM, ATTENDANCE;

    public static boolean isValid(String value) {
        if (value == null) return false;
        for (ComplianceType t : values()) if (t.name().equalsIgnoreCase(value)) return true;
        return false;
    }
}
