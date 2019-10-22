package com.dengage.sdk.notification.models;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.dengage.sdk.notification.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Event extends ModelBase {
    @SerializedName("accountId")
    private String accountId;

    @SerializedName("key")
    private String key;

    @SerializedName("eventTable")
    private String eventTable;

    @SerializedName("eventDetails")
    private Map<String,Object> eventDetails;


    private transient  Gson gson = new Gson();

    public Event(String accountId, String key, String eventTable, Map<String,Object> details) {

        if(accountId == null || TextUtils.isEmpty(accountId))
            throw new IllegalArgumentException("Argument null: accountId");

        if(key == null || TextUtils.isEmpty(key))
            throw new IllegalArgumentException("Argument null: key");

        if(eventTable == null || TextUtils.isEmpty(eventTable))
            throw new IllegalArgumentException("Argument null: eventTable");

        if(details == null)
            throw new IllegalArgumentException("Argument null: details ");

        this.accountId = accountId;
        this.key = key;
        this.eventTable = eventTable;
        this.eventDetails = details;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
