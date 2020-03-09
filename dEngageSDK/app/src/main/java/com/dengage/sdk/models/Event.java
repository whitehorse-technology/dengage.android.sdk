package com.dengage.sdk.models;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Event extends ModelBase {
    @SerializedName("integrationKey")
    private String integrationKey;

    @SerializedName("key")
    private String key;

    @SerializedName("eventTable")
    private String eventTable;

    @SerializedName("eventDetails")
    private Map<String,Object> eventDetails;


    private transient  Gson gson = new Gson();

    public Event(String integrationKey, String eventTable, String key, Map<String,Object> details) {

        if(integrationKey == null || TextUtils.isEmpty(integrationKey))
            throw new IllegalArgumentException("Argument null: integrationKey");

        if(key == null || TextUtils.isEmpty(key))
            throw new IllegalArgumentException("Argument null: key");

        if(eventTable == null || TextUtils.isEmpty(eventTable))
            throw new IllegalArgumentException("Argument null: eventTable");

        if(details == null)
            throw new IllegalArgumentException("Argument null: details ");

        this.integrationKey = integrationKey;
        this.key = key;
        this.eventTable = eventTable;
        this.eventDetails = details;
    }
}
