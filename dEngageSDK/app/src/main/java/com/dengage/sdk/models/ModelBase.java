package com.dengage.sdk.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public abstract class ModelBase {

    @SerializedName("integrationKey")
    public String integrationKey = "";

    public String getIntegrationKey() {
        return this.integrationKey;
    }

    public void setIntegrationKey(String integrationKey) {
        this.integrationKey = integrationKey;
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        return gson.toJson(this);
    }
}
