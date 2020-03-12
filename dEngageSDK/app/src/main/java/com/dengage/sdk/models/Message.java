package com.dengage.sdk.models;

import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class Message {

    @SerializedName("messageId")
    private int messageId = 0;

    @SerializedName("smallIcon")
    private String smallIcon = "";

    @SerializedName("largeIcon")
    private String largeIcon = "";

    @SerializedName("messageSource")
    private String messageSource = "";

    @SerializedName("transactionId")
    private String transactionId = "";

    @SerializedName("messageDetails")
    private String messageDetails = "";

    @SerializedName("mediaUrl")
    private String mediaUrl = "";

    @SerializedName("targetUrl")
    private String targetUrl = "";

    @SerializedName("title")
    private String title = "";

    @SerializedName("subTitle")
    private String subTitle = "";

    @SerializedName("message")
    private String message = "";

    @SerializedName("badge")
    private Boolean badge = false;

    @SerializedName("badgeCount")
    private int badgeCount = 0;

    @SerializedName("sound")
    private String sound = "";

    @SerializedName("customParams")
    private CustomParam[] customParams = null;

    @SerializedName("medias")
    private Media[] medias = null;

    private transient Gson gson = new Gson();

    public Message(@NonNull Map<String,String> bundle) {
        setProperties(bundle);
    }

    public Message(@NonNull Bundle bundle) {
        Map<String, String> params = new HashMap<String, String>();
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if(value != null) {
                params.put(key, value.toString());
            }
        }
        setProperties(params);
    }

    private void setProperties(@NonNull Map<String,String> bundle) {

        if (bundle.get("messageId") != null && !TextUtils.isEmpty(bundle.get("messageId")))
            messageId = Integer.parseInt(bundle.get("messageId"));

        if (bundle.get("smallIcon") != null && !TextUtils.isEmpty(bundle.get("smallIcon")))
            smallIcon = bundle.get("smallIcon");

        if (bundle.get("largeIcon") != null && !TextUtils.isEmpty(bundle.get("largeIcon")))
            largeIcon = bundle.get("largeIcon");

        if (bundle.get("messageSource") != null && !TextUtils.isEmpty(bundle.get("messageSource")))
            messageSource = bundle.get("messageSource");

        if (bundle.get("messageDetails") != null && !TextUtils.isEmpty(bundle.get("messageDetails")))
            messageDetails = bundle.get("messageDetails");

        if (bundle.get("mediaUrl") != null && !TextUtils.isEmpty(bundle.get("mediaUrl")))
            mediaUrl = bundle.get("mediaUrl");

        if (bundle.get("targetUrl") != null && !TextUtils.isEmpty(bundle.get("targetUrl")))
            targetUrl = bundle.get("targetUrl");

        if (bundle.get("title") != null && !TextUtils.isEmpty(bundle.get("title")))
            title = bundle.get("title");

        if (bundle.get("transactionId") != null && !TextUtils.isEmpty(bundle.get("transactionId")))
            transactionId = bundle.get("transactionId");

        if (bundle.get("subTitle") != null && !TextUtils.isEmpty(bundle.get("subTitle")))
            subTitle = bundle.get("subTitle");

        if (bundle.get("message") != null && !TextUtils.isEmpty(bundle.get("message")))
            message = bundle.get("message");

        if (bundle.get("badge") != null & !TextUtils.isEmpty(bundle.get("badge")))
            badge = Boolean.parseBoolean(bundle.get("badge"));

        if (bundle.get("badgeCount") != null && !TextUtils.isEmpty(bundle.get("badgeCount")))
            badgeCount = Integer.parseInt( bundle.get("badgeCount") );

        if (bundle.get("sound") != null && !TextUtils.isEmpty(bundle.get("sound")))
            sound = bundle.get("sound");

        if (bundle.get("customParams") != null)
            customParams = gson.fromJson(bundle.get("customParams"), CustomParam[].class);

        if (bundle.get("media") != null)
            medias = gson.fromJson(bundle.get("media"), Media[].class);

        if (TextUtils.isEmpty(this.mediaUrl) && medias != null && medias.length > 0) {
            mediaUrl = medias[0].getUrl();
        }

        if (TextUtils.isEmpty(this.targetUrl) && medias != null && medias.length > 0) {
            targetUrl = medias[0].getTarget();
        }
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public String getSmallIcon() {
        return smallIcon;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getMessageDetails() {
        return messageDetails;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getMessage() {
        return this.message;
    }

    public Boolean getBadge(){
        return this.badge;
    }

    public int getBadgeCount() {
        return this.badgeCount;
    }

    public String getSound() {
        return this.sound;
    }

    public CustomParam[] getCustomParams() {
        return customParams;
    }

    public Media[] getMedias() {
        return medias;
    }

    public String getMessageSource() {
        return messageSource;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}