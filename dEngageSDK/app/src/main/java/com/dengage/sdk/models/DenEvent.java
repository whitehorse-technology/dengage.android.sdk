package com.dengage.sdk.models;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class DenEvent extends ModelBase {

    @SerializedName("eventName")
    private String eventName;

    @SerializedName("sessionId")
    private String sessionId;

    @SerializedName("persistentId")
    private String persistentId;

    @SerializedName("language")
    private String language;

    @SerializedName("screenWidth")
    private int screenWidth;

    @SerializedName("screenHeight")
    private int screenHeight;

    @SerializedName("timeZone")
    private int timeZone;

    @SerializedName("sdkVersion")
    private String sdkVersion;

    @SerializedName("os")
    private String os;

    @SerializedName("md")
    private String model;

    @SerializedName("mn")
    private String manufacturer;

    @SerializedName("br")
    private String brand;

    @SerializedName("deviceUniqueId")
    private String deviceUniqueId;

    @SerializedName("referrer")
    private String referrer;

    @SerializedName("location")
    private String location;

    @SerializedName("pushToken")
    private String pushToken;

    @SerializedName("eventDetails")
    private Map<String,Object> params;

    public DenEvent(String eventName, String sessionId, String persistentId) {

        if(eventName == null || TextUtils.isEmpty(eventName))
            throw new IllegalArgumentException("Argument null: eventName");

        if(sessionId == null || TextUtils.isEmpty(sessionId))
            throw new IllegalArgumentException("Argument null: sessionId");

        if(persistentId == null || TextUtils.isEmpty(persistentId))
            throw new IllegalArgumentException("Argument null: persistentId");

        this.eventName = eventName;
        this.sessionId = sessionId;
        this.persistentId = persistentId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
