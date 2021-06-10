package com.dengage.sdk.models;

import com.dengage.sdk.cache.GsonHolder;
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
        try {
            return GsonHolder.INSTANCE.getGson().toJson(this);
        } catch (IncompatibleClassChangeError e) {
            e.printStackTrace();
            return null;
        }
    }
}
