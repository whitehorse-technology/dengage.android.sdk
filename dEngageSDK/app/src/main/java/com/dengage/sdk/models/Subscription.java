package com.dengage.sdk.models;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class Subscription extends ModelBase {

    @SerializedName("token")
    private String token = "";

    @SerializedName("appVersion")
    private String appVersion = "";

    @SerializedName("os")
    private transient String os = "";

    @SerializedName("osVersion")
    private transient String osVersion = "";

    @SerializedName("deviceType")
    private transient String deviceType = "";

    @SerializedName("deviceName")
    private transient String deviceName = "";

    @SerializedName("local")
    private transient String local = "";

    @SerializedName("sdkVersion")
    private String sdkVersion = "";

    @SerializedName("udid")
    private String deviceId = "";

    @SerializedName("advertisingId")
    private String advertisingId = "";

    @SerializedName("gsm")
    private transient String gsm = "";

    @SerializedName("email")
    private transient String email = "";

    @SerializedName("carrierId")
    private String carrierId = "";

    @SerializedName("contactKey")
    private String contactKey = "";

    @SerializedName("permission")
    private Boolean permission = true;

    @SerializedName("extra")
    private transient Map<String, Object> extra = new HashMap<>();

    @SerializedName("tokenSaved")
    private transient boolean tokenSaved = false;

    @SerializedName("trackingPermission")
    private boolean trackingPermission = true;

    @SerializedName("tokenType")
    private String tokenType = "A";

    @SerializedName("webSubscription")
    private String webSubscription = null;

    @SerializedName("subscriptionUri")
    private transient String subscriptionUri;

    @SerializedName("tokenType")
    private String testGroup = "";

    @SerializedName("userAgent")
    private transient String userAgent = "";

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

    public Boolean getTokenSaved() {
        return this.tokenSaved;
    }

    public void setTokenSaved(Boolean value) {
        this.tokenSaved = value;
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

    public void setAdvertingId(String advertisingId) {
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

    public String getSubscriptionUri() {
        return subscriptionUri;
    }

    public void setSubscriptionUri(String subscriptionUri) {
        this.subscriptionUri = subscriptionUri;
    }

    public boolean isValidSubscriptionUri() {
        return this.getSubscriptionUri() != null && !TextUtils.isEmpty(this.getSubscriptionUri());
    }

    public String getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(String testGroup) {
        this.testGroup = testGroup;
    }
}
