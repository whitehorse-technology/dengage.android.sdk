package com.dengage.sdk.models;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class DenEvent extends ModelBase {

    @SerializedName("eventName")
    private String eventName;

    @SerializedName("sessionId")
    private String sessionId;

    @SerializedName("udid")
    private String deviceId;

    @SerializedName("testGroup")
    private String testGroup;

    @SerializedName("contactKey")
    private String contactKey;

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String persistentId) {
        this.deviceId = persistentId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(String testGroup) {
        this.testGroup = testGroup;
    }

    public String getContactKey() {
        return contactKey;
    }

    public void setContactKey(String contactKey) {
        this.contactKey = contactKey;
    }

    @Override
    public String toJson() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.setExclusionStrategies(new CustomExclusionStrategy()).create();
        JsonElement doc = gson.toJsonTree(this);
        if(this.params != null) {
            JsonObject obj = doc.getAsJsonObject();
            for (Map.Entry<String, Object> param : this.getParams().entrySet()) {
                if(param.getValue() instanceof Map) {
                    JsonElement e = gson.toJsonTree(param.getValue(), Map.class);
                    obj.add(param.getKey(), e);
                }
                else if(param.getValue() instanceof Boolean) {
                    obj.addProperty(param.getKey(), (Boolean)param.getValue());
                }
                else if(param.getValue() instanceof Number) {
                    obj.addProperty(param.getKey(), (Number)param.getValue());
                }
                else if(param.getValue() == null) {
                    obj.add(param.getKey(), JsonNull.INSTANCE);
                }
                else {
                    obj.addProperty(param.getKey(), param.getValue().toString());
                }
            }
        }
        return gson.toJson(doc);
    }

    private class CustomExclusionStrategy implements ExclusionStrategy {

        public boolean shouldSkipField(FieldAttributes f) {
            return f.getName().equals("integrationKey");
        }

        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

    }
}
