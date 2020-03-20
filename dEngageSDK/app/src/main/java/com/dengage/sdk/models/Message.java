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

    @SerializedName("messageSource")
    private String messageSource = "";

    @SerializedName("transactionId")
    private String transactionId = "";

    @SerializedName("messageDetails")
    private String messageDetails = "";

    @SerializedName("mediaUrl")
    private String mediaUrl = "";

    @SerializedName("media")
    private Media[] media = null;

    @SerializedName("targetUrl")
    private String targetUrl = "";

    @SerializedName("title")
    private String title = "";

    @SerializedName("subTitle")
    private String subText = "";

    @SerializedName("message")
    private String message = "";

    @SerializedName("badge")
    private Boolean badge = false;

    @SerializedName("badgeCount")
    private int badgeCount = 0;

    @SerializedName("sound")
    private String sound = "";

    @SerializedName("notificationType")
    private NotificationType notificationType = NotificationType.RICH;

    @SerializedName("customParams")
    private CustomParam[] customParams = null;

    @SerializedName("carouselContent")
    private CarouselItem[] carouselContent = null;

    @SerializedName("actionButtons")
    private ActionButton[] actionButtons = null;

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

        if(bundle.get("notificationType") != null && !TextUtils.isEmpty(bundle.get("notificationType")))
            notificationType = NotificationType.valueOf(bundle.get("notificationType"));

        if (bundle.get("messageId") != null && !TextUtils.isEmpty(bundle.get("messageId")))
            messageId = Integer.parseInt(bundle.get("messageId"));

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
            subText = bundle.get("subTitle");

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

        if (bundle.get("carouselContent") != null)
            carouselContent = gson.fromJson(bundle.get("carouselContent"), CarouselItem[].class);

        if (bundle.get("actionButtons") != null)
            actionButtons = gson.fromJson(bundle.get("actionButtons"), ActionButton[].class);

        if (bundle.get("media") != null)
            media = gson.fromJson(bundle.get("media"), Media[].class);

        if(media != null && media.length > 0) {
            mediaUrl = media[0].getUrl();
        }
    }

    public NotificationType getNotificationType() {
        return notificationType;
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

    public String getSubText() {
        return subText;
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

    public CarouselItem[] getCarouselContent() {
        return carouselContent;
    }

    public ActionButton[] getActionButtons() {
        return actionButtons;
    }

    public String getMessageSource() {
        return messageSource;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}