package com.dengage.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.dengage.sdk.models.Event;
import com.dengage.sdk.models.Message;
import com.dengage.sdk.models.Open;
import com.dengage.sdk.models.Subscription;
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
import java.util.Map;

public class DengageManager {

    private static Logger logger = Logger.getInstance();
    @SuppressLint("MayStaticFieldLeak")
    private static Context _context;

    DengageManager() {

    }

    /**
     * Initiator method
     * <p>
     * Use to initiate dEngage MobileManager with the integration key.
     * </p>
     * @param integrationKey Application key which you took from dEngage platform.
     */
    public static void setConfig(String integrationKey, Context context) {

        logger.Verbose("setConfig method called");
        logger.Verbose("Open API Endpoint:          "+ Constants.openApiEndpoint);
        logger.Verbose("Subscription API Endpoint:  "+ Constants.subsApiEndpoint);
        logger.Verbose("Transactional API Endpoint: "+ Constants.transOpenApiEndpoint);
        logger.Verbose("Event API Endpoint:         "+ Constants.eventApiEndpoint);

        if(integrationKey == null || TextUtils.isEmpty(integrationKey)) {
            throw new IllegalArgumentException("Argument null: integrationKey");
        }

        if(context == null) {
            throw new IllegalArgumentException("Argument null: context");
        }

        try {

            _context = context;
            getSubscription();
            setIntegrationKey(integrationKey);
            FirebaseApp.initializeApp(_context);
            syncSubscription();

        } catch (Exception e) {
            logger.Error("setConfig: "+ e.getMessage());
        }
    }

    /**
     * Gets Device Identifier
     * @return String
     */
    public static String getDeviceId() {
        Subscription subscription = getSubscription();
        logger.Verbose("getDeviceId method is called.");
        logger.Debug("getDeviceId: "+ subscription.getUdid());
        return subscription.getUdid();
    }

    /**
     * Gets Advertising Identifier
     * @return String
     */
    public static String getAdvertisingId() {
        Subscription subscription = getSubscription();
        logger.Verbose("getAdvertisingId method is called.");
        logger.Debug("getAdvertisingId: "+ subscription.getAdid());
        return subscription.getAdid();
    }

    /**
     * Gets Subscription Token
     * @return String
     */
    public static String getToken() {
        Subscription subscription = getSubscription();
        logger.Verbose("getToken method is called.");
        logger.Debug("getToken: "+ subscription.getToken());
        return subscription.getToken();
    }

    /**
     * Gets dEngage User Key
     * @return String
     */
    public static String getContactKey() {
        Subscription subscription = getSubscription();
        logger.Verbose("getContactKey method is called.");
        logger.Debug("getContactKey: "+ subscription.getContactKey());
        return subscription.getContactKey();
    }

    /**
     * Set User Push Permission
     * <p>
     * Use to set permission to a user
     * </p>
     * @param permission True/False
     */
    public static void setPermission(Boolean permission) {
        logger.Verbose("setPermission method is called");
        try {
            Subscription subscription = getSubscription();
            subscription.setPermission(permission);
            logger.Debug("permission: "+ permission);
            saveSubscription(subscription);
        } catch (Exception e) {
            logger.Error("setPermission: "+ e.getMessage());
        }
    }

    /**
     * Set contact key of the user.
     * <p>
     * Use to set dEngage key to a user.
     * </p>
     * @param contactKey user key
     */
    public static void setContactKey(String contactKey) {
        logger.Verbose("setContactKey method is called");
        try {
            Subscription subscription = getSubscription();
            subscription.setContactKey(contactKey);
            logger.Debug("contactKey: "+ contactKey);
            saveSubscription(subscription);
        } catch (Exception e) {
            logger.Error("setContactKey: "+ e.getMessage());
        }
    }


    /**
     * Set App Integration Key
     * <p>
     * Use to set integration key at runtime.
     * </p>
     * @param key dEngage Integration Key
     */
    public static void setIntegrationKey(String key) {
        logger.Verbose("setIntegrationKey method is called");
        try {
            Subscription subscription = getSubscription();
            logger.Debug("setIntegrationKey: "+ key);
            subscription.setIntegrationKey(key);
            saveSubscription(subscription);
        } catch (Exception e) {
            logger.Error("setIntegrationKey: "+ e.getMessage());
        }
    }

    /**
     * Subscribe User
     * <p>
     * Use to register a user to dEngage. Only required when you perform a manuel GCM registration.
     * </p>
     * @param token GCM Token
     */
    public static void subscribe(String token) {
        logger.Verbose("subscribe(token) method is called");
        try {
            Subscription subscription = getSubscription();
            subscription.setToken(token);
            if(TextUtils.isEmpty(token))
                throw new IllegalArgumentException("Argument empty: token");
            saveSubscription(subscription);
            logger.Debug("subscribe(token): " + token);
            syncSubscription();
        } catch (Exception e) {
            logger.Error("subscribe(token): "+ e.getMessage());
        }
    }

    /**
     * Sync user information with dEngage.
     * <p>
     * Use to send the latest information to dEngage. If you set any property or perform a logout, you are advised to call this method.
     * </p>
     */
    public static void syncSubscription() {
        logger.Verbose("syncSubscription method is called");

        try {

            Subscription subscription = getSubscription();
            logger.Debug("tokenSaved: " + subscription.getTokenSaved());

            FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    logger.Verbose("Token retrieving canceled");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    logger.Error("Token retrieving failed: " + e.getMessage());
                }
            })
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        logger.Error("Firebase InstanceId Failed: " + task.getException().getMessage());
                        return;
                    }

                    String token = task.getResult().getToken();
                    logger.Debug("Token retrieved: " + token);

                    Subscription subscription = getSubscription();
                    subscription.setToken(token);

                    if(!subscription.getTokenSaved() ) {
                        logger.Debug("syncSubscription: " + subscription.toJson());
                        subscription.setTokenSaved(true);
                        RequestAsync req = new RequestAsync(Constants.subsApiEndpoint, Utils.getUserAgent(_context), subscription, Subscription.class);
                        req.execute();
                    }

                    saveSubscription(subscription);
                }
            });

            AdvertisingIdWorker adIdWorker = new AdvertisingIdWorker();
            adIdWorker.execute();

            if( subscription.getTokenSaved() ) {
                RequestAsync req = new RequestAsync(Constants.subsApiEndpoint, Utils.getUserAgent(_context), subscription, Subscription.class);
                req.execute();
                logger.Debug("syncSubscription: " + subscription.toJson());
            }

        } catch (Exception e) {
            logger.Error("syncSubscription: "+ e.getMessage());
        }
    }


    /**
     * Sends open event
     * <p>
     * Use to open report when a GCM message is received. Only required when you perform a manuel GCM registration.
     * </p>
     * @param message The dEngage message object.
     */
    public static void sendOpenEvent(Message message) {
        logger.Verbose("sendOpenEvent method is called");
        try {
            Subscription subscription = getSubscription();

            if(message == null) throw new IllegalArgumentException("Argument null: message");

            String source = message.getMessageSource();
            if (!Constants.MESSAGE_SOURCE.equals(source))  return;

            Open openSignal = new Open();
            openSignal.setIntegrationKey(subscription.getIntegrationKey());
            openSignal.setMessageId(message.getMessageId());
            openSignal.setMessageDetails(message.getMessageDetails());
            openSignal.setTransactionId(message.getTransactionId());

            logger.Debug("sendOpenEvent: " + openSignal.toJson());

            if (!TextUtils.isEmpty(message.getTransactionId())) {
                RequestAsync req = new RequestAsync(Constants.transOpenApiEndpoint, Utils.getUserAgent(_context), openSignal, Open.class);
                req.execute();
            }
            else {
                RequestAsync req = new RequestAsync(Constants.openApiEndpoint, Utils.getUserAgent(_context), openSignal, Open.class);
                req.execute();
            }

        } catch (Exception e) {
            logger.Error("sendOpenEvent: "+ e.getMessage());
        }
    }

    /**
     * Sends a custom event
     * <p>
     * Use to hit a custom event report.
     * </p>
     * @param tableName The event table name of the schema.
     * @param key Value of the event key.
     * @param data Additional key-value data which is correspond table column name-value.
     */
    public static void sendCustomEvent(String tableName, String key, Map<String,Object> data) {
        logger.Verbose("sendCustomEvent method is called");
        try {
            Subscription subscription = getSubscription();
            Event event = new Event(subscription.getIntegrationKey(), tableName, key, data);
            logger.Debug("sendCustomEvent: " + event.toJson());
            RequestAsync req = new RequestAsync(Constants.eventApiEndpoint, Utils.getUserAgent(_context), event, Event.class);
            req.execute();
        } catch (Exception e) {
            logger.Error("sendCustomEvent: "+ e.getMessage());
        }
    }

    /**
     * Sends a device event
     * <p>
     * Use to hit a device event report.
     * </p>
     * @param tableName The event table name of the schema.
     * @param data Additional key-value data which is correspond table column name-value.
     */
    public static void sendDeviceEvent(String tableName, Map<String, Object> data) {
        logger.Verbose("sendDeviceEvent method is called");
        try {
            Subscription subscription = getSubscription();
            Event event = new Event(subscription.getIntegrationKey(), tableName, getDeviceId(), data);
            logger.Debug("sendDeviceEvent: " + event.toJson());
            RequestAsync req = new RequestAsync(Constants.eventApiEndpoint, Utils.getUserAgent(_context), event, Event.class);
            req.execute();
        } catch (Exception e) {
            logger.Error("sendDeviceEvent: "+ e.getMessage());
        }
    }

    /**
     * Console Log
     * <p>
     * Use to show logs on console.
     * </p>
     * @param status True/False
     */
    public static void setLogStatus(Boolean status) {
        logger.setLogStatus(status);
    }

    private static Subscription getSubscription() {
        try {

            if (Utils.hasSubscription(_context)) {
                return new Gson().fromJson(Utils.getSubscription(_context), Subscription.class);
            } else {
                return new Subscription();
            }

        } catch (Exception ex) {
            logger.Error("Exception on getSubscription: "+ ex.getMessage());
            return new Subscription();
        }
    }

    private static void saveSubscription(Subscription subscription) {
        logger.Verbose("saveSubscription method is called");
        try {

            subscription.setUdid(Utils.getDeviceId(_context));
            subscription.setCarrierId(Utils.carrier(_context));
            subscription.setAppVersion(Utils.appVersion(_context));
            subscription.setLocal(Utils.local(_context));
            subscription.setOs(Utils.osType());
            subscription.setOsVersion(Utils.osVersion());
            subscription.setSdkVersion(com.dengage.sdk.BuildConfig.VERSION_NAME);
            subscription.setDeviceName(Utils.deviceName());
            subscription.setDeviceType(Utils.deviceType());

            Utils.saveSubscription(_context, subscription.toJson());
        } catch (Exception e) {
            logger.Error("saveSubscription: "+ e.getMessage());
        }
    }

    private static class AdvertisingIdWorker extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... params) {
            logger.Debug("Getting advertising ID");
            AdvertisingIdClient.Info adInfo;
            String advertisingId = "";
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(_context);
                if (!adInfo.isLimitAdTrackingEnabled())
                    advertisingId = adInfo.getId();
            } catch (GooglePlayServicesNotAvailableException e) {
                logger.Error("GooglePlayServicesNotAvailableException: "+ e.getMessage());
            } catch (GooglePlayServicesRepairableException e) {
                logger.Error("GooglePlayServicesRepairableException: "+ e.getMessage());
            } catch (Exception e) {
                logger.Error("Exception: "+e.getMessage());
            }
            return advertisingId;
        }

        @Override
        protected void onPostExecute(String adId) {
            Subscription subscription = getSubscription();
            subscription.setAdid(adId);
            saveSubscription(subscription);
        }
    }
}


