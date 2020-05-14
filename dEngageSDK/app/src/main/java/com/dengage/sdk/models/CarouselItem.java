package com.dengage.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CarouselItem {

    @SerializedName("id")
    private String id = "";
    @SerializedName("title")
    private String title = "";
    @SerializedName("desc")
    private String description = "";
    @SerializedName("mediaUrl")
    private String mediaUrl = "";
    @SerializedName("targetUrl")
    private String targetUrl = "";

    private String type = "";
    private String mediaFileLocation;
    private String mediaFileName;

    public CarouselItem(String mediaUrl) {
        this(null, null, null, mediaUrl, null);
    }

    public CarouselItem() {
        this(null, null, null, null, null);
    }

    public CarouselItem(String id, String title, String description, String mediaUrl, String targetUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
        this.targetUrl = targetUrl;
    }

    protected CarouselItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        mediaUrl = in.readString();
        mediaFileLocation = in.readString();
        mediaFileName = in.readString();
        type = in.readString();
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaFileLocation() {
        return mediaFileLocation;
    }

    public void setMediaFileLocation(String mediaFileLocation) {
        this.mediaFileLocation = mediaFileLocation;
    }

    public void setMediaFileName(String mediaFileName) {
        this.mediaFileName = mediaFileName;
    }

    public String getMediaFileName() {
        return mediaFileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
