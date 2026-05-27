package com.edulink.identityservice.entity;

/** Spec §4.1 — allowed values for User.status. Persisted as String for migration safety. */
public enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED;

    public static boolean isValid(String value) {
        if (value == null) return false;
        for (UserStatus s : values()) if (s.name().equalsIgnoreCase(value)) return true;
        return false;
    }
}
