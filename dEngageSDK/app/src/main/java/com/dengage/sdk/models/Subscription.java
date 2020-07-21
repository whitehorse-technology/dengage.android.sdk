package com.dengage.sdk.models;

import android.text.TextUtils;

import com.dengage.sdk.Constants;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Subscription extends ModelBase {

    @SerializedName("token")
    private String token = "";

    @SerializedName("firebaseIntegrationKey")
    private transient String firebaseIntegrationKey = "";

    @SerializedName("huaweiIntegrationKey")
    private transient String huaweiIntegrationKey = "";

    @SerializedName("appVersion")
    private String appVersion = "";

    @SerializedName("sdkVersion")
    private String sdkVersion = "";

    @SerializedName("udid")
    private String deviceId = "";

    @SerializedName("advertisingId")
    private String advertisingId = "";

    @SerializedName("carrierId")
    private String carrierId = "";

    @SerializedName("contactKey")
    private String contactKey = "";

    @SerializedName("permission")
    private Boolean permission = true;

    @SerializedName("trackingPermission")
    private boolean trackingPermission = true;

    @SerializedName("tokenType")
    private String tokenType = "A";

    @SerializedName("webSubscription")
    private String webSubscription = null;

    @SerializedName("testGroup")
    private String testGroup = "";

    @SerializedName("userAgent")
    private transient String userAgent = "";

    @SerializedName("dnCampId")
    private String campaignId = "";

    @SerializedName("dnSendId")
    private String sendId = "";

    @SerializedName("dnCampDate")
    private Date campaignDate = null;

    @SerializedName("dnCampaignDuration")
    private int campaignDuration = Constants.DEFAULT_CAMP_ATTR_DURATION;

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAdvertisingId() {
        return this.advertisingId;
    }

    public void setAdvertisingId(String advertisingId) {
        this.advertisingId = advertisingId;
    }

    public String getContactKey() {
        return this.contactKey;
    }

    public void setContactKey(String contactKey) {
        this.contactKey = contactKey;
    }

    public Boolean getPermission() {
        return this.permission;
    }

    public void setPermission(Boolean permission) {
        this.permission = permission;
    }

    public String getCarrierId() {
        return this.carrierId;
    }

    public void setCarrierId(String carrierId) {
        this.carrierId = carrierId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(String testGroup) {
        this.testGroup = testGroup;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getWebSubscription() {
        return webSubscription;
    }

    public void setWebSubscription(String webSubscription) {
        this.webSubscription = webSubscription;
    }

    public boolean getTrackingPermission() {
        return trackingPermission;
    }

    public void setTrackingPermission(boolean trackingPermission) {
        this.trackingPermission = trackingPermission;
    }

    public void setFirebaseIntegrationKey(String firebaseIntegrationKey) {
        this.firebaseIntegrationKey = firebaseIntegrationKey;
    }

    public void setHuaweiIntegrationKey(String huaweiIntegrationKey) {
        this.huaweiIntegrationKey = huaweiIntegrationKey;
    }

    public String getFirebaseIntegrationKey() {
        return firebaseIntegrationKey;
    }

    public String getHuaweiIntegrationKey() {
        return huaweiIntegrationKey;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public Date getCampaignDate() {
        return campaignDate;
    }

    public void setCampaignDate(Date campaignDate) {
        this.campaignDate = campaignDate;
    }

    public int getCampaignDuration() {
        return campaignDuration;
    }

    public void setCampaignDuration(int campaignDuration) {
        this.campaignDuration = campaignDuration;
    }
}
