package com.dengage.sdk.models;

import com.google.gson.annotations.SerializedName;

public class ActionButton {
    @SerializedName("id")
    private String id;
    @SerializedName("text")
    private String text;
    @SerializedName("icon")
    private String icon;
    @SerializedName("targetUrl")
    private String targetUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
