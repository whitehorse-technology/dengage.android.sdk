package com.dengage.sdk.models;

public enum NotificationType {
    TEXT("TEXT"),
    RICH("RICH"),
    CAROUSEL("CAROUSEL");

    private final String name;

    NotificationType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName != null) && name.equals(otherName);
    }

    public String toString() {
        return name;
    }

}