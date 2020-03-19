package com.dengage.sdk.models;

import com.google.gson.annotations.SerializedName;

public class CarouselItem {

    @SerializedName("title")
    private String title;
    @SerializedName("desc")
    private String description;
    @SerializedName("mediaUrl")
    private String mediaUrl;
    @SerializedName("targetUrl")
    private String targetUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
