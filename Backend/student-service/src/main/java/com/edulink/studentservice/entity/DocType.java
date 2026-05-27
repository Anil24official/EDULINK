package com.edulink.studentservice.entity;

/** Spec §4.2 — allowed values for StudentDocument.docType. Persisted as String. */
public enum DocType {
    IDProof, ReportCard;

    public static boolean isValid(String value) {
        if (value == null) return false;
        for (DocType d : values()) if (d.name().equalsIgnoreCase(value)) return true;
        return false;
    }
}
