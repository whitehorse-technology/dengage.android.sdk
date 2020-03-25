package com.dengage.sdk.models;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class DenEvent extends ModelBase {

    @SerializedName("eventName")
    private String eventName;

    @SerializedName("sessionId")
    private String sessionId;

    @SerializedName("persistentId")
    private String persistentId;

    @SerializedName("testGroup")
    private String testGroup;

    @SerializedName("memberId")
    private String memberId;

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

    public String getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(String testGroup) {
        this.testGroup = testGroup;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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
