package com.dengage.sdk.notification.models;

import com.google.gson.annotations.SerializedName;

public abstract class ModelBase {
    @SerializedName("token")
    private String token;

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @SerializedName("appAlias")
    private String appAlias;

    public String getAppAlias() {
        return this.appAlias;
    }

    public void setAppAlias(String appAlias) {
        this.appAlias = appAlias;
    }
}
