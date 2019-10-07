package com.dengage.sdk.notification.enums;

public enum PushType {
    Text("Text"),
    Image("Image"),
    Video("Video"),
    Background("Background"),
    Survey("Survey"),
    Animated("Animated");

    private final String name;

    PushType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName != null) && name.equals(otherName);
    }

    public String toString() {
        return name;
    }

}