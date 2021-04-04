package com.dengage.sdk.models;

import androidx.annotation.RestrictTo;

import com.google.gson.annotations.SerializedName;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class AbstractOpen extends ModelBase {

    @SerializedName("buttonId")
    private String buttonId;

    @SerializedName("itemId")
    private String itemId;

    @SerializedName("messageId")
    private int messageId;

    @SerializedName("messageDetails")
    private String messageDetails;

    @SerializedName("userAgent")
    private transient String userAgent = "";

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

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

    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
