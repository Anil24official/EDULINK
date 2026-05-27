package com.edulink.notificationservice.entity;

/** Spec §4.9 — allowed values for Notification.category. Persisted as String. */
public enum NotificationCategory {
    ENROLLMENT, COURSE, EXAM, COMPLIANCE;

    public static boolean isValid(String value) {
        if (value == null) return false;
        for (NotificationCategory c : values()) if (c.name().equalsIgnoreCase(value)) return true;
        return false;
    }
}
