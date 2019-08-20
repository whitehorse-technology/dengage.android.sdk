package com.dengage.sdk.notification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DnsResolver;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.dengage.sdk.BuildConfig;
import com.dengage.sdk.notification.helpers.RequestHelper;
import com.dengage.sdk.notification.helpers.Utils;
import com.dengage.sdk.notification.logging.Logger;
import com.dengage.sdk.notification.models.Location;
import com.dengage.sdk.notification.models.Message;
import com.dengage.sdk.notification.models.Open;
import com.dengage.sdk.notification.models.Subscription;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

public class MobileManager {

    private static MobileManager instance;
    private Subscription subscription;
    private Context context;
    private String API_HOST = (BuildConfig.DEBUG ? "http://10.200.0.100:5000" : "http://10.200.0.100:5000");
    private final String OPEN_SIGNAL_API_URL = API_HOST + "/api/mobile/open";
    private final String SUBS_SIGNAL_API_URL = API_HOST + "/api/mobile/subscription";

    private MobileManager(String appAlias, final Context context) {

        if(appAlias == null) {
            throw new IllegalArgumentException("Argument null: appAlias");
        }

        if(context == null) {
            throw new IllegalArgumentException("Argument null: context");
        }

        this.context = context;
        this.subscription = getSubscription(context);
        this.subscription.setAppAlias(appAlias);
    }

    /**
     * Initiator method
     * <p>
     * Use to initiate dEngage MobileManager with the application alias
     *
     * @param appAlias Application alias that you defined on dEngage platform.
     */
    public static MobileManager createInstance(String appAlias, Context context) {
        if (instance == null) {
            instance = new MobileManager(appAlias, context);
        }
        Logger.Debug("createInstance.createInstance appAlias: " + instance.subscription.getAppAlias());
        return instance;
    }

    /**
     * Current Manager Instance
     * <p>
     * Use to get dEngage MobileManager current instance
     */
    public static MobileManager getInstance() {
        return instance;
    }

    /**
     * Register to GCM
     * <p>
     * Use to get a token from Firebase
     *
     */
    public void register() {
        Logger.Debug("MobileManager.register appAlias: " + instance.subscription.getAppAlias());
        FirebaseApp.initializeApp(this.context);
    }

    private void setToken(String token) {
        this.subscription.setToken(token);
        this.setSubscription(this.context);
    }

    private void setUdid(String udid) {
        this.subscription.setUdid(udid);
        this.setSubscription(this.context);
    }

    /**
     * Retention service
     * <p>
     * Use to report when a GCM message is received. Only required when you perform a manuel GCM registration.
     *
     * @param message Message Id
     */
    public void open(final Message message) {

        this.subscription = getSubscription(this.context);

        Logger.Debug("MobileManager.open message: " + message.toJson());
        Logger.Debug("MobileManager.open token: " + this.subscription.getToken());

        if(TextUtils.isEmpty(this.subscription.getToken())) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Logger.Error("FirebaseInstanceId Failed: " + task.getException().getMessage());
                        return;
                    }

                    String token = task.getResult().getToken();
                    Logger.Debug("Token retrieved: " + token);

                    setToken(token);

                    if(!TextUtils.isEmpty(token)) {
                        sendOpen(message);
                    } else Logger.Error("Token is empty! Please use the other subscribe method.");
                }
            });

        } else {
            sendOpen(message);
        }
    }

    private void sendOpen(Message message) {
        Open openSignal = new Open();
        openSignal.setAppAlias(this.subscription.getAppAlias());
        openSignal.setToken(this.subscription.getToken());
        openSignal.setMessageId(message.getMessageId());
        openSignal.setMessageDetails(message.getMessageDetails());
        RequestHelper.getInstance().sendRequestAsync(OPEN_SIGNAL_API_URL, openSignal, Open.class);
    }
    /**
     * Subscribe User
     * <p>
     * Use to register a user to dEngage.
     *
     */
    public void subscribe() {
        Logger.Debug("MobileManager.subscribe token: " + this.subscription.getToken());
        sync();
    }

    /**
     * Subscribe User
     * <p>
     * Use to register a user to dEngage. Only required when you perform a manuel GCM registration.
     *
     * @param token   GCM Token
     */
    public void subscribe(String token) {
        setToken(token);
        Logger.Debug("MobileManager.subscribe token: " + token);
        sync();
    }

    /**
     * Sync user information with dEngage
     * <p>
     * Use to send the latest information to dEngage. If you set any property or perform a logout, you are advised to call this method.
     *
     */
    public void sync() {
        this.subscription = getSubscription(this.context);

        Logger.Debug("MobileManager.sync token: " + this.subscription.getToken());
        if (!TextUtils.isEmpty(this.subscription.getToken())) {
            RequestHelper.getInstance().sendRequestAsync(SUBS_SIGNAL_API_URL, this.subscription, Subscription.class);
        } else {
            Logger.Error("MobileManager.sync: token is empty.");
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Logger.Error("FirebaseInstanceId Failed: " + task.getException().getMessage());
                        return;
                    }

                    String token = task.getResult().getToken();
                    Logger.Debug("Token retrieved: " + token);

                    setToken(token);

                    if (!TextUtils.isEmpty(token)) sync();
                    else Logger.Error("Token is empty! Please use the other subscribe method.");
                }
            });
        }
    }

    /**
     * Set Application Version
     * <p>
     * Use to set application version
     *
     * @param appVersion Application version
     */
    public void setAppVersion(String appVersion) {
        this.subscription.setAppVersion(appVersion);
        Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
    }

    /**
     * Set User Push Permission
     * <p>
     * Use to set permission to a user
     *
     * @param permission True/False
     */
    public void setPermission(Boolean permission) {
        this.subscription.setPermission(permission);
        Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
    }

    /**
     * Set User Twitter Id
     * <p>
     * Use to set twitter id to a user.
     *
     * @param twitterId Twitter Id
     */
    public void setTwitterId(String twitterId) {
        this.subscription.setTwitterId(twitterId);
        Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
    }

    /**
     * Set User Email
     * <p>
     * Use to set email to a user.
     *
     * @param email Email of a user
     */
    public void setEmail(String email) {
        this.subscription.setEmail(email);
        Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
    }

    /**
     * Set User Facebook Id
     * <p>
     * Use to set facebook id to a user.
     *
     * @param facebookId Facebook Id
     */
    public void setFacebookId(String facebookId) {
        this.subscription.setFacebookId(facebookId);
        Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
    }

    /**
     * Set User Location
     * <p>
     * Use to set last known location to a user.
     *
     * @param latitude  Latitude
     * @param longitude Longitude
     */
    public void setLocation(double latitude, double longitude) {
        this.subscription.setLocation(new Location(latitude, longitude));
        Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
    }

    /**
     * Set User dEngage Id
     * <p>
     * Use to set dEngage id to a user.
     *
     * @param contactKey user key
     */
    public void setContactKey(String contactKey) {
        this.subscription.setContactKey(contactKey);
        Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
    }

    /**
     * Set User Phone
     * <p>
     * Use to set phone number to a user.
     *
     * @param msisdn phone number
     */
    public void setGsm(String msisdn) {
        this.subscription.setGsm(msisdn);
        Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
    }

    /**
     * Set User Property
     * <p>
     * Use to set a custom property to a user.
     *
     * @param key   key for the property
     * @param value value for the property
     */
    public void setContactProperty(String key, String value) {
        setSubscriptionProperty(key, value);
        Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
    }

    /**
     * Remove Contact Properties
     * <p>
     * If you have set contact properties before, you can remove them here. Preferred when the contact logs out from the app. Use sync afterwards.
     */
    public void removeContactProperties() {
        this.subscription.removeAll();
        Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
    }

    private Subscription getSubscription(Context context) {
        Subscription subscription = new Subscription();

        if (Utils.hasPrefString(context, Constants.SUBSCRIPTION_KEY)) {
            subscription = new Gson().fromJson(Utils.getPrefString(context, Constants.SUBSCRIPTION_KEY), Subscription.class);
            subscription.setFirstTime(0);
        } else {
            subscription.setFirstTime(1);
        }

        if( TextUtils.isEmpty( subscription.getUdid() )) {
            AdvertisingIdWorker adIdWorker = new AdvertisingIdWorker(context);
            adIdWorker.execute();
        }

        return subscription;
    }

    private void setSubscription(Context context) {
        this.subscription.setCarrierId(Utils.carrier(context));
        if(TextUtils.isEmpty(this.subscription.getAppVersion()))
            this.subscription.setAppVersion(Utils.appVersion(context));
        this.subscription.setLocal(Utils.local(context));
        this.subscription.setOs(Utils.osType());
        this.subscription.setOsVersion(Utils.osVersion());
        this.subscription.setSdkVersion(Constants.SDK_VERSION);
        this.subscription.setDeviceName(Utils.deviceName());
        this.subscription.setDeviceType(Utils.deviceType());
        try {
            Utils.savePrefString(context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    private void setSubscriptionProperty(String key, Object value) {
        this.subscription.add(key, value);
    }

    private class AdvertisingIdWorker extends AsyncTask<Void, String, String> {

        Context context;
        public AdvertisingIdWorker(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            AdvertisingIdClient.Info idInfo = null;
            String advertisingId = "";
            try {
                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                advertisingId = idInfo.getId();
            } catch (GooglePlayServicesNotAvailableException e) {
                Logger.Error(e.getMessage());
            } catch (GooglePlayServicesRepairableException e) {
                Logger.Error(e.getMessage());
            } catch (Exception e) {
                Logger.Error(e.getMessage());
            }

            return advertisingId;
        }

        @Override
        protected void onPostExecute(String udid) {
            MobileManager.getInstance().subscription.setUdid(udid);
            MobileManager.getInstance().setSubscription(context);
        }
    }
}
