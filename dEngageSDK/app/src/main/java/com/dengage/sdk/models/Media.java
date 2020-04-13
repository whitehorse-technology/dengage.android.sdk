package com.dengage.sdk.models;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Media {
    @SerializedName("url")
    private String url = "";

    @SerializedName("target")
    private String target = "";

    public Media() {

    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
