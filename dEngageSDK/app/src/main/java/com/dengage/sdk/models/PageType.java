package com.dengage.sdk.models;

public enum PageType {
    searchPage("searchPage"),
    categoryPage("categoryPage"),
    promotionPage("promotionPage");

    private final String name;

    PageType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName != null) && name.equals(otherName);
    }

    public String toString() {
        return name;
    }
}
