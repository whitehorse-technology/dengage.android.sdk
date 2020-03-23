package com.dengage.sdk.models;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class EcEvent extends ModelBase {

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
    private transient Map<String,Object> params;

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

    @Override
    public String toJson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setExclusionStrategies(new FieldExclusionStrategy());
        Gson gson = builder.create();
        JsonElement doc = gson.toJsonTree(this);
        if(this.params != null) {
            JsonObject obj = doc.getAsJsonObject();
            for (Map.Entry<String, Object> param : this.getParams().entrySet()) {
                if(param.getValue() instanceof Boolean) {
                    obj.addProperty(param.getKey(), (Boolean)param.getValue());
                }
                else if(param.getValue() instanceof Number) {
                    obj.addProperty(param.getKey(), (Number)param.getValue());
                }
                else {
                    obj.addProperty(param.getKey(), param.getValue().toString());
                }
            }
        }
        return gson.toJson(doc);
    }

    private static class FieldExclusionStrategy implements ExclusionStrategy {
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getName().equals("integrationKey");
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
