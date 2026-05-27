package com.edulink.examservice.entity;

/** Spec §4.5 — allowed values for Exam.examType. Persisted as String. */
public enum ExamType {
    MIDTERM, FINAL;

    public static boolean isValid(String value) {
        if (value == null) return false;
        for (ExamType t : values()) if (t.name().equalsIgnoreCase(value)) return true;
        return false;
    }
}
