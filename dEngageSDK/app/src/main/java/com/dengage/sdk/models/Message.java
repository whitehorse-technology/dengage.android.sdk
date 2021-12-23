package com.dengage.sdk.models;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @SerializedName("dengageCampId")
    private int campaignId = 0;

    @SerializedName("dengageCampName")
    private String campName = "";

    @SerializedName("dengageSendId")
    private int sendId = 0;


    @SerializedName("notificationType")
    private NotificationType notificationType = NotificationType.RICH;

    @SerializedName("customParams")
    private CustomParam[] customParams = null;

    @SerializedName("carouselContent")
    private CarouselItem[] carouselContent = null;

    @SerializedName("actionButtons")
    private ActionButton[] actionButtons = null;

    @SerializedName("addToInbox")
    private Boolean addToInbox = false;

    @SerializedName("expireDate")
    @Nullable
    private String expireDate;

    private transient Gson gson = new Gson();

    public Message(@NonNull Map<String, String> bundle) {
        setProperties(bundle);
    }

    public Message(@NonNull Bundle bundle) {
        Map<String, String> params = new HashMap<String, String>();
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (value != null) {
                params.put(key, value.toString());
            }
        }
        setProperties(params);
    }

    private void setProperties(@NonNull Map<String, String> bundle) {

        if (bundle.get("notificationType") != null && !bundle.get("notificationType").isEmpty())
            notificationType = NotificationType.valueOf(bundle.get("notificationType"));

        if (bundle.get("notificationType") != null && !bundle.get("notificationType").isEmpty())
            notificationType = NotificationType.valueOf(bundle.get("notificationType"));

        if (bundle.get("messageId") != null && !bundle.get("messageId").isEmpty())
            messageId = Integer.parseInt(bundle.get("messageId"));

        if (bundle.get("dengageCampId") != null && !bundle.get("dengageCampId").isEmpty())
            campaignId = Integer.parseInt(bundle.get("dengageCampId"));

        if (bundle.get("dengageCampName") != null && !bundle.get("dengageCampName").isEmpty())
            campName = bundle.get("dengageCampName");

        if (bundle.get("dengageSendId") != null && !bundle.get("dengageSendId").isEmpty())
            sendId = Integer.parseInt(bundle.get("dengageSendId"));

        if (bundle.get("messageSource") != null && !bundle.get("messageSource").isEmpty())
            messageSource = bundle.get("messageSource");

        if (bundle.get("messageDetails") != null && !bundle.get("messageDetails").isEmpty())
            messageDetails = bundle.get("messageDetails");

        if (bundle.get("mediaUrl") != null && !bundle.get("mediaUrl").isEmpty())
            mediaUrl = bundle.get("mediaUrl");

        if (bundle.get("targetUrl") != null && !bundle.get("targetUrl").isEmpty())
            targetUrl = bundle.get("targetUrl");

        if (bundle.get("title") != null && !bundle.get("title").isEmpty())
            title = bundle.get("title");

        if (bundle.get("transactionId") != null && !bundle.get("transactionId").isEmpty())
            transactionId = bundle.get("transactionId");

        if (bundle.get("subTitle") != null && !bundle.get("subTitle").isEmpty())
            subText = bundle.get("subTitle");

        if (bundle.get("message") != null && !bundle.get("message").isEmpty())
            message = bundle.get("message");

        if (bundle.get("badge") != null && !bundle.get("badge").isEmpty())
            badge = Boolean.parseBoolean(bundle.get("badge"));

        if (bundle.get("badgeCount") != null && !bundle.get("badgeCount").isEmpty())
            badgeCount = Integer.parseInt(bundle.get("badgeCount"));

        if (bundle.get("sound") != null && !bundle.get("sound").isEmpty())
            sound = bundle.get("sound");

        if (bundle.get("customParams") != null)
            customParams = gson.fromJson(bundle.get("customParams"), CustomParam[].class);

        if (bundle.get("carouselContent") != null)
            carouselContent = gson.fromJson(bundle.get("carouselContent"), CarouselItem[].class);

        if (bundle.get("actionButtons") != null)
            actionButtons = gson.fromJson(bundle.get("actionButtons"), ActionButton[].class);

        if (bundle.get("media") != null)
            media = gson.fromJson(bundle.get("media"), Media[].class);

        if (media != null && media.length > 0) {
            mediaUrl = media[0].getUrl();
        }

        if (bundle.get("addToInbox") != null && !bundle.get("addToInbox").isEmpty())
            addToInbox = Boolean.parseBoolean(bundle.get("addToInbox"));

        if (bundle.get("expireDate") != null && !bundle.get("expireDate").isEmpty())
            expireDate = bundle.get("expireDate");
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

    public Boolean getBadge() {
        return this.badge;
    }

    public int getBadgeCount() {
        return this.badgeCount;
    }

    public String getSound() {
        return this.sound;
    }

    public int getSendId() {
        return sendId;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public CustomParam[] getCustomParams() {
        return customParams;
    }

    public CarouselItem[] getCarouselContent() {
        return carouselContent;
    }

    public void setCarouselContent(CarouselItem[] carouselContent) {
        this.carouselContent = carouselContent;
    }

    public ActionButton[] getActionButtons() {
        return actionButtons;
    }

    public String getMessageSource() {
        return messageSource;
    }

    public Boolean getAddToInbox() {
        return addToInbox;
    }

    public @Nullable
    String getExpireDate() {
        return expireDate;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static Message fromJson(String json) {
        return new Gson().fromJson(json, Message.class);
    }

    public String getCampName() {
        return campName;
    }

}
