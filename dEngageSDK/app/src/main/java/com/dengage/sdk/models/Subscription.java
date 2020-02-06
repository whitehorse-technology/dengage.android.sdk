package com.dengage.sdk.models;

import android.text.TextUtils;

import com.dengage.sdk.models.Location;
import com.dengage.sdk.models.ModelBase;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class Subscription extends ModelBase {

    @SerializedName("appVersion")
    private String appVersion = "";

    @SerializedName("os")
    private transient  String os = "";

    @SerializedName("osVersion")
    private transient  String osVersion = "";

    @SerializedName("deviceType")
    private transient  String deviceType = "";

    @SerializedName("deviceName")
    private transient  String deviceName = "";

    @SerializedName("local")
    private transient  String local = "";

    @SerializedName("sdkVersion")
    private String sdkVersion = "";

    @SerializedName("udid")
    private String udid = "";

    @SerializedName("advertisingId")
    private String adid = "";

    @SerializedName("twitterId")
    private transient  String twitterId = "";

    @SerializedName("facebookId")
    private transient  String facebookId = "";

    @SerializedName("gsm")
    private transient  String gsm = "";

    @SerializedName("email")
    private transient  String email = "";

    @SerializedName("carrierId")
    private String carrierId = "";

    @SerializedName("contactKey")
    private String contactKey = "";

    @SerializedName("permission")
    private Boolean permission = true;

    @SerializedName("location")
    private transient Location location;

    @SerializedName("extra")
    private transient Map<String, Object> extra = new HashMap<>();

    @SerializedName("tokenSaved")
    private boolean tokenSaved = false;

    public void add(String key, Object value) {
        extra.put(key, value);
    }

    public void addAll(Map<String, Object> extras) {
        extra.putAll(extras);
    }

    public void removeAll() {
        this.extra.clear();
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(getToken()) & !TextUtils.isEmpty(getIntegrationKey()) & !TextUtils.isEmpty(getUdid());
    }

    public Boolean getTokenSaved() {
        return this.tokenSaved;
    }

    public void setTokenSaved(Boolean value) {
        this.tokenSaved = value;
    }

    public String getUdid() {
        return this.udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getAdid() {
        return this.adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
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

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public Location getLocation() {
        return location;
    }

    public String getEmail() {
        return email;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getGsm() {
        return gsm;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setGsm(String gsm) {
        this.gsm = gsm;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
