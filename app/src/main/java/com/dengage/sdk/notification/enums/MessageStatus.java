package com.dengage.sdk.notification.enums;

public enum MessageStatus {
    Read("READ"),
    Received("OPEN");

    private final String name;

    MessageStatus(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName != null) && name.equals(otherName);
    }

    public String toString() {
        return name;
    }
}
