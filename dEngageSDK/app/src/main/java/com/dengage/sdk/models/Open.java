package com.dengage.sdk.models;

import com.dengage.sdk.models.ModelBase;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Open extends ModelBase {
    @SerializedName("messageId")
    private int messageId;

    @SerializedName("messageDetails")
    private String messageDetails;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getMessageDetails() {
        return messageDetails;
    }

    public void setMessageDetails(String messageDetails) {
        this.messageDetails = messageDetails;
    }
}
