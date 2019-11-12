package com.dengage.sdk.notification;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import com.dengage.sdk.BuildConfig;
import com.dengage.sdk.notification.helpers.RequestHelper;
import com.dengage.sdk.notification.helpers.Utils;
import com.dengage.sdk.notification.logging.Logger;
import com.dengage.sdk.notification.models.Event;
import com.dengage.sdk.notification.models.Location;
import com.dengage.sdk.notification.models.Message;
import com.dengage.sdk.notification.models.Open;
import com.dengage.sdk.notification.models.Subscription;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

public class dEngageMobileManager {

    private static dEngageMobileManager instance;
    public Subscription subscription;
    private Context context;

    private final String apiHostDev                             = "https://pushdev.dengage.com";
    private final String apiHostTest                            = "https://pushtest.dengage.com";
    private final String apiHostProd                            = "https://push.dengage.com";
    private final String subsApiSuffix                          = "/api/device/subscription";
    private final String openApiSuffix
            = "/api/mobile/open";
    private final String transactionalOpenApiSuffix             = "/api/transactional/mobile/open";

    private final String eventApiHostDev                        = "https://eventdev.dengage.com";
    private final String eventApiHostTest                       = "https://eventtest.dengage.com";
    private final String eventApiHostProd                       = "https://event.dengage.com";
    private final String eventApiSuffix                         = "/api/Event";

    private String openApiEndpoint;
    private String subsApiEndpoint;
    private String transactionalOpenApiEndpoint;
    private String eventApiEndpoint;

    private dEngageMobileManager(String integrationKey, final Context context) {

        if(integrationKey == null) {
            throw new IllegalArgumentException("Argument null: integrationKey");
        }

        if(context == null) {
            throw new IllegalArgumentException("Argument null: context");
        }

        try {

            if(BuildConfig.ENVIRONMENT == "dev") {
                openApiEndpoint                     = apiHostDev + openApiSuffix;
                subsApiEndpoint                     = apiHostDev + subsApiSuffix;
                transactionalOpenApiEndpoint        = apiHostDev + transactionalOpenApiSuffix;
                eventApiEndpoint                    = eventApiHostDev + eventApiSuffix;
                Logger.Verbose("Open API Endpoint:          "+ openApiEndpoint);
                Logger.Verbose("Subscription API Endpoint:  "+ subsApiEndpoint);
                Logger.Verbose("Transactional API Endpoint: "+ transactionalOpenApiEndpoint);
                Logger.Verbose("Event API Endpoint:         "+ eventApiEndpoint);
            } else if(BuildConfig.ENVIRONMENT == "test") {
                openApiEndpoint                     = apiHostTest + openApiSuffix;
                subsApiEndpoint                     = apiHostTest + subsApiSuffix;
                transactionalOpenApiEndpoint        = apiHostTest + transactionalOpenApiSuffix;
                eventApiEndpoint                    = eventApiHostTest + eventApiSuffix;
                Logger.Verbose("Open API Endpoint:          "+ openApiEndpoint);
                Logger.Verbose("Subscription API Endpoint:  "+ subsApiEndpoint);
                Logger.Verbose("Transactional API Endpoint: "+ transactionalOpenApiEndpoint);
                Logger.Verbose("Event API Endpoint:         "+ eventApiEndpoint);
            } else if(BuildConfig.ENVIRONMENT == "prod") {
                openApiEndpoint                     = apiHostProd + openApiSuffix;
                subsApiEndpoint                     = apiHostProd + subsApiSuffix;
                transactionalOpenApiEndpoint        = apiHostProd + transactionalOpenApiSuffix;
                eventApiEndpoint                    = eventApiHostProd + eventApiSuffix;
                Logger.Verbose("Open API Endpoint:          "+ openApiEndpoint);
                Logger.Verbose("Subscription API Endpoint:  "+ subsApiEndpoint);
                Logger.Verbose("Transactional API Endpoint: "+ transactionalOpenApiEndpoint);
                Logger.Verbose("Event API Endpoint:         "+ eventApiEndpoint);
            } else {
                throw new IllegalArgumentException("Argument null: BuildConfig.ENVIRONMENT, expected: dev,test or prod.");
            }

            this.context = context;
            this.subscription  = new Subscription();
            this.subscription.setIntegrationKey(integrationKey);
            this.subscription = getSubscription(context);

            sync();

        } catch (Exception e) {
            Logger.Error("dEngageMobileManager: "+ e.getMessage());
        }

        Logger.Verbose("Created dEnaggeMobileManager.");
    }

    /**
     * Gets current SDK environment.
     * @return String
     */
    public String getEnvironment() {
        Logger.Verbose("getEnvironment method is called.");
        Logger.Debug("getEnvironment: "+ Constants.ENVIRONMENT);
        return Constants.ENVIRONMENT;
    }

    /**
     * Gets dEngage Android SDK version you use.
     * @return String
     */
    public String getSdkVersion() {
        Logger.Verbose("getSdkVersion method is called.");
        Logger.Debug("getSdkVersion: "+ Constants.SDK_VERSION);
        return Constants.SDK_VERSION;
    }

    /**
     * Gets your application version.
     * @return String
     */
    public String getAppVersion() {
        Logger.Verbose("getAppVersion method is called.");
        String appVersion = Utils.appVersion(context);
        Logger.Debug("getAppVersion: "+ appVersion);
        return appVersion;
    }


    /**
     *
     * @return
     */
    public String getSubscriptionJson() {
        return this.subscription.toJson();
    }

    /**
     * Initiator method
     * <p>
     * Use to initiate dEngage MobileManager with the application alias.
     * </p>
     * @param integrationKey Application key which you got from dEngage platform.
     */
    public static dEngageMobileManager createInstance(String integrationKey, Context context) {
        Logger logger = new Logger(context);
        if (instance == null) {
            instance = new dEngageMobileManager(integrationKey, context);
        }
        return instance;
    }

    /**
     * Current Manager Instance
     * <p>
     * Use to get dEngage MobileManager current instance.
     * </p>
     */
    public static dEngageMobileManager getInstance() {
        Logger.Verbose("getInstance method is called");
        return instance;
    }

    /**
     * Register to GCM
     * <p>
     * Use to get a token from Firebase.
     * </p>
     */
    public void register() {
        Logger.Verbose("register method is called");
        try {
            Logger.Debug("MobileManager.register integrationKey: " + instance.subscription.getIntegrationKey());
            FirebaseApp.initializeApp(this.context);
        } catch (Exception e) {
            Logger.Error("register: "+ e.getMessage());
        }
    }

    /**
     * Retention service
     * <p>
     * Use to open report when a GCM message is received. Only required when you perform a manuel GCM registration.
     * </p>
     * @param message The message object.
     */
    public void open(final Message message) {
        Logger.Verbose("open method is called");
        try {
            this.subscription = getSubscription(this.context);

            Logger.Debug("MobileManager.open message: " + message.toJson());
            Logger.Debug("MobileManager.open token: " + this.subscription.getToken());

            Open openSignal = new Open();
            openSignal.setIntegrationKey(this.subscription.getIntegrationKey());
            openSignal.setMessageId(message.getMessageId());
            openSignal.setMessageDetails(message.getMessageDetails());
            openSignal.setTransactionId(message.getTransactionId());
            if (!TextUtils.isEmpty(message.getTransactionId()))
                RequestHelper.getInstance().sendRequestAsync(transactionalOpenApiEndpoint, openSignal, Open.class);
            else
                RequestHelper.getInstance().sendRequestAsync(openApiEndpoint, openSignal, Open.class);
        } catch (Exception e) {
            Logger.Error("open: "+ e.getMessage());
        }
    }

    /**
     * Subscribe User
     * <p>
     * Use to register a user to dEngage.
     * </p>
     */
    public void subscribe() {
        Logger.Verbose("subscribe method is called");
        try {
            Logger.Debug("MobileManager.subscribe token: " + this.subscription.getToken());
            sync();
        } catch (Exception e) {
            Logger.Error("subscribe: "+ e.getMessage());
        }
    }

    /**
     * Subscribe User
     * <p>
     * Use to register a user to dEngage. Only required when you perform a manuel GCM registration.
     * </p>
     * @param token  GCM Token
     */
    public void subscribe(String token) {
        Logger.Verbose("subscribe(token) method is called");
        try {
            setToken(token);
            Logger.Debug("MobileManager.subscribe token: " + token);
            sync();
        } catch (Exception e) {
            Logger.Error("subscribe(token): "+ e.getMessage());
        }
    }

    /**
     * Sync user information with dEngage.
     * <p>
     * Use to send the latest information to dEngage. If you set any property or perform a logout, you are advised to call this method.
     * </p>
     */
    public void sync() {
        Logger.Verbose("sync method is called");
        try {
            this.subscription = getSubscription(this.context);

            Logger.Debug("MobileManager.sync key: " + this.subscription.getIntegrationKey());
            Logger.Debug("MobileManager.sync token: " + this.subscription.getToken());
            Logger.Debug("MobileManager.sync udid: " + this.subscription.getUdid());
            Logger.Debug("MobileManager.sync adid: " + this.subscription.getAdid());

            // if (!TextUtils.isEmpty(this.subscription.getToken())) {
            RequestHelper.getInstance().sendRequestAsync(subsApiEndpoint, this.subscription, Subscription.class);
            // } else {
            //     Logger.Error("MobileManager.sync: token is empty.");
            //}
        } catch (Exception e) {
            Logger.Error("sync: "+ e.getMessage());
        }
    }

    /**
     * Retention service
     * <p>
     * Use to hit an event report.
     * </p>
     * @param event The event object.
     */
    public void sendEvent(final Event event) {
        Logger.Verbose("sendEvent method is called");
        try {
            this.subscription = getSubscription(this.context);
            RequestHelper.getInstance().sendRequestAsync(eventApiEndpoint, event, Event.class);
        } catch (Exception e) {
            Logger.Error("sendEvent: "+ e.getMessage());
        }
    }

    /**
     * Set Application Version
     * <p>
     * Use to set application version.
     * </p>
     * @param appVersion Application version
     */
    public void setAppVersion(String appVersion) {
        Logger.Verbose("setAppVersion method is called");
        try {
            this.subscription.setAppVersion(appVersion);
            Logger.Debug("appVersion: "+ appVersion);
            Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            Logger.Error("setAppVersion: "+ e.getMessage());
        }
    }

    /**
     * Set User Push Permission
     * <p>
     * Use to set permission to a user
     * </p>
     * @param permission True/False
     */
    public void setPermission(Boolean permission) {
        Logger.Verbose("setPermission method is called");
        try {
            this.subscription.setPermission(permission);
            Logger.Debug("permission: "+ permission);
            Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            Logger.Error("setPermission: "+ e.getMessage());
        }
    }

    /**
     * Set User Twitter Id
     * <p>
     * Use to set twitter id to a user.
     * </p>
     * @param twitterId Twitter Id
     */
    public void setTwitterId(String twitterId) {
        Logger.Verbose("setTwitterId method is called");
        try {
            this.subscription.setTwitterId(twitterId);
            Logger.Debug("twitterId: "+ twitterId);
            Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            Logger.Error("setTwitterId: "+ e.getMessage());
        }
    }

    /**
     * Set User Email
     * <p>
     * Use to set email to a user.
     * </p>
     * @param email Email of a user
     */
    public void setEmail(String email) {
        Logger.Verbose("setEmail method is called");
        try {
            this.subscription.setEmail(email);
            Logger.Debug("email: "+ email);
            Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            Logger.Error("setEmail: "+ e.getMessage());
        }
    }

    /**
     * Set User Facebook Id
     * <p>
     * Use to set facebook id to a user.
     * </p>
     * @param facebookId Facebook Id
     */
    public void setFacebookId(String facebookId) {
        Logger.Verbose("setFacebookId method is called");
        try {
            this.subscription.setFacebookId(facebookId);
            Logger.Debug("facebookId: "+ facebookId);
            Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            Logger.Error("setFacebookId: "+ e.getMessage());
        }
    }

    /**
     * Set User Location
     * <p>
     * Use to set last known location to a user.
     * </p>
     * @param latitude  Latitude
     * @param longitude Longitude
     */
    public void setLocation(double latitude, double longitude) {
        Logger.Verbose("setLocation method is called");
        try {
            this.subscription.setLocation(new Location(latitude, longitude));
            Logger.Debug("lat: "+ latitude + ", long: "+ longitude);
            Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            Logger.Error("setLocation: "+ e.getMessage());
        }
    }

    /**
     * Set User dEngage contact key.
     * <p>
     * Use to set dEngage key to a user.
     * </p>
     * @param contactKey user key
     */
    public void setContactKey(String contactKey) {
        Logger.Verbose("setContactKey method is called");
        try {
            this.subscription.setContactKey(contactKey);
            Logger.Debug("contactKey: "+ contactKey);
            Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            Logger.Error("setContactKey: "+ e.getMessage());
        }
    }

    /**
     * Set User Phone
     * <p>
     * Use to set phone number to a user.
     * </p>
     * @param msisdn phone number
     */
    public void setGsm(String msisdn) {
        Logger.Verbose("setGsm method is called");
        try {
            this.subscription.setGsm(msisdn);
            Logger.Debug("msisdn: "+ msisdn);
            Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            Logger.Error("setGsm: "+ e.getMessage());
        }
    }

    /**
     * Set User Device ID
     * <p>
     * Use to set device id to a user.
     * </p>
     * @param udid Device ID
     */
    public void setUdId(String udid) {
        Logger.Verbose("setUdId method is called");
        try {
            Logger.Debug("DeviceId: "+ udid);
            this.subscription.setUdid(udid);
            this.setSubscription(this.context);
        } catch (Exception e) {
            Logger.Error("setUdId: "+ e.getMessage());
        }
    }

    /**
     * Set User Advertising ID
     * <p>
     * Use to set advertising id to a user.
     * </p>
     * @param adid Advertising ID
     */
    public void setAdId(String adid) {
        Logger.Verbose("setAdid method is called");
        try {
            Logger.Debug("AdvertisingId: "+ adid);
            this.subscription.setAdid(adid);
            this.setSubscription(this.context);
        } catch (Exception e) {
            Logger.Error("setAdid: "+ e.getMessage());
        }
    }

    /**
     * Set App Integration Key
     * <p>
     * Use to set integration key at runtime.
     * </p>
     * @param key dEngage Integration Key
     */
    public void setIntegrationKey(String key) {
        Logger.Verbose("setIntegrationKey method is called");
        try {
            Logger.Debug("setIntegrationKey: "+ key);
            this.subscription.setIntegrationKey(key);
            this.setSubscription(this.context);
        } catch (Exception e) {
            Logger.Error("setIntegrationKey: "+ e.getMessage());
        }
    }

    /**
     * Set User Property
     * <p>
     * Use to set a custom property to a user.
     * </p>
     * @param key   key for the property
     * @param value value for the property
     */
    public void setContactProperty(String key, String value) {
        Logger.Verbose("setContactProperty method is called");
        try {
            setSubscriptionProperty(key, value);
            Logger.Debug("key, value: "+ key +", "+ value+"");
            Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            Logger.Error("setContactProperty: "+ e.getMessage());
        }
    }

    /**
     * Remove Contact Properties
     * <p>
     * If you have set contact properties before, you can remove them here. Preferred when the contact logs out from the app. Use sync afterwards.
     * </p>
     */
    public void removeContactProperties() {
        Logger.Verbose("removeContactProperties method is called");
        try {
            this.subscription.removeAll();
            Utils.savePrefString(this.context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
            Logger.Error("removeContactProperties: "+ e.getMessage());
        }
    }

    private void setToken(String token) {
        Logger.Verbose("setToken method is called");
        try {
            this.subscription.setToken(token);
            this.setSubscription(this.context);
        } catch (Exception e) {
            Logger.Error("setToken: "+ e.getMessage());
        }
    }

    private Subscription getSubscription(final Context context) {
        Logger.Verbose("getSubscription method is called");
        if (Utils.hasPrefString(context, Constants.SUBSCRIPTION_KEY)) {
            Logger.Verbose(Constants.SUBSCRIPTION_KEY +" key is not empty. Subscription model is getting from json file.");
            subscription = new Gson().fromJson(Utils.getPrefString(context, Constants.SUBSCRIPTION_KEY), Subscription.class);
            subscription.setFirstTime(0);
        } else {
            subscription.setFirstTime(1);
        }

        setUdId(Utils.udid(this.context));

        // if( TextUtils.isEmpty( subscription.getAdid() )) {
        AdvertisingIdWorker adIdWorker = new AdvertisingIdWorker(context);
        adIdWorker.execute();
        //}

        if( TextUtils.isEmpty(subscription.getToken())) {
            Logger.Verbose("Token is empty. The token is getting from Firebase.");
            FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Logger.Verbose("Token onCanceled");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Logger.Verbose("Token onFailure");
                    Logger.Error("Token retrieved: " + e.getMessage());
                }
            })
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    Logger.Verbose("Token onComplete");
                    if (!task.isSuccessful()) {
                        Logger.Error("FirebaseInstanceId Failed: " + task.getException().getMessage());
                        return;
                    }

                    String token = task.getResult().getToken();
                    Logger.Debug("Token retrieved: " + token);

                    if(!TextUtils.isEmpty(token)) {
                        subscribe(token);
                    }
                }
            });
        }

        return subscription;
    }

    private void setSubscription(Context context) {
        Logger.Verbose("setSubscription method is called");
        try {

            this.subscription.setCarrierId(Utils.carrier(context));
            this.subscription.setAppVersion(Utils.appVersion(context));
            this.subscription.setLocal(Utils.local(context));
            this.subscription.setOs(Utils.osType());
            this.subscription.setOsVersion(Utils.osVersion());
            this.subscription.setSdkVersion(Constants.SDK_VERSION);
            this.subscription.setDeviceName(Utils.deviceName());
            this.subscription.setDeviceType(Utils.deviceType());

            Utils.savePrefString(context, Constants.SUBSCRIPTION_KEY, this.subscription.toJson());
        } catch (Exception e) {
           Logger.Error("setSubscription: "+ e.getMessage());
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
            Logger.Debug("Getting advertising ID");
            AdvertisingIdClient.Info adInfo = null;
            String advertisingId = "";
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                if (!adInfo.isLimitAdTrackingEnabled())
                    advertisingId = adInfo.getId();
            } catch (GooglePlayServicesNotAvailableException e) {
                Logger.Error("GooglePlayServicesNotAvailableException: "+ e.getMessage());
            } catch (GooglePlayServicesRepairableException e) {
                Logger.Error("GooglePlayServicesRepairableException: "+ e.getMessage());
            } catch (Exception e) {
                Logger.Error("Exception: "+e.getMessage());
            }
            return advertisingId;
        }

        @Override
        protected void onPostExecute(String adid) {
            setAdId(adid);
        }
    }
}
