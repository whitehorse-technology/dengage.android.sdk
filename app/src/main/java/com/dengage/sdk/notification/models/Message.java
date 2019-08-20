package com.dengage.sdk.notification.models;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class Message {

    private int messageId = 0;
    private String messageDetails = "";
    private String mediaUrl = "";
    private String targetUrl = "";
    private String title = "";
    private String subTitle = "";
    private String message = "";
    private Boolean badge = false;
    private int badgeCount = 0;
    private String sound = "";
    private CustomParam[] customParams = null;
    private Media[] medias = null;
    private Gson gson = new Gson();

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
        if(bundle.get("messageId") != null & !TextUtils.isEmpty(bundle.get("messageId")))
            messageId = Integer.parseInt(bundle.get("messageId"));

        if(bundle.get("messageDetails") != null & !TextUtils.isEmpty(bundle.get("messageDetails")))
            messageDetails = bundle.get("messageDetails");

        if(bundle.get("mediaUrl") != null & !TextUtils.isEmpty(bundle.get("mediaUrl")))
            mediaUrl = bundle.get("mediaUrl");

        if(bundle.get("targetUrl") != null & !TextUtils.isEmpty(bundle.get("targetUrl")))
            targetUrl = bundle.get("targetUrl");

        if(bundle.get("title") != null & !TextUtils.isEmpty(bundle.get("title")))
            title = bundle.get("title");

        if(bundle.get("subTitle") != null & !TextUtils.isEmpty(bundle.get("subTitle")))
            subTitle = bundle.get("subTitle");

        if(bundle.get("message") != null & !TextUtils.isEmpty(bundle.get("message")))
            message = bundle.get("message");

        if(bundle.get("badge") != null & !TextUtils.isEmpty(bundle.get("badge")))
            badge = Boolean.parseBoolean(bundle.get("badge"));

        if(bundle.get("badgeCount") != null & !TextUtils.isEmpty(bundle.get("badgeCount")))
            badgeCount = Integer.parseInt(bundle.get("badgeCount"));

        if(bundle.get("sound") != null & !TextUtils.isEmpty(bundle.get("sound")))
            sound = bundle.get("sound");

        if(bundle.get("customParams") != null )
            customParams = gson.fromJson(bundle.get("customParams"), CustomParam[].class);

        if(bundle.get("media") != null )
            medias = gson.fromJson(bundle.get("media"), Media[].class);

        if(TextUtils.isEmpty(this.mediaUrl) & medias != null & medias.length > 0) {
            mediaUrl = medias[0].getUrl();
        }

        if(TextUtils.isEmpty(this.targetUrl) & medias != null & medias.length > 0) {
            targetUrl = medias[0].getTarget();
        }
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

    public String getSubTitle() {
        return subTitle;
    }

    public String getMessage() {
        return this.message;
    }

    public Boolean getBadge(){
        return this.badge;
    }

    public int getBadgeCount() { return this.badgeCount;  }

    public String getSound() {
        return this.sound;
    }

    public CustomParam[] getCustomParams() {
        return customParams;
    }

    public Media[] getMedias() {
        return medias;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}