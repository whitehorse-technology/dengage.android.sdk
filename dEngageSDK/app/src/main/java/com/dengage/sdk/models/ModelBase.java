package com.dengage.sdk.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public abstract class ModelBase {
    @SerializedName("token")
    private String token = "";

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @SerializedName("integrationKey")
    private String integrationKey = "";

    public String getIntegrationKey() {
        return this.integrationKey;
    }

    public void setIntegrationKey(String integrationKey) {
        this.integrationKey = integrationKey;
    }

    @SerializedName("transactionId")
    private String transactionId;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @SerializedName("userAgent")
    private transient String userAgent;

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String agent) {
        this.userAgent = agent;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
