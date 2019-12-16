package com.dengage.sdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
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

    private static DengageManager INSTANCE = new DengageManager();

    private Subscription _subscription;

    private Context _context;

    private String openApiEndpoint          = "https://push.dengage.com/api/mobile/open";
    private String subsApiEndpoint          = "https://pushdev.dengage.com/api/device/subscription";
    private String eventApiEndpoint         = "https://event.dengage.com/api/event";
    private String transOpenApiEndpoint     = "https://push.dengage.com/api/api/transactional/mobile/open";

    private static RequestHelper request = new RequestHelper();

    private DengageManager() {
    }

    /**
     * Initiator method
     * <p>
     * Use to initiate dEngage MobileManager with the application key.
     * </p>
     * @param integrationKey Application key which you got from dEngage platform.
     */
    public static void setConfig(String integrationKey, Context context) {

        Logger.INSTANCE.Verbose("setConfig method called");
        Logger.INSTANCE.Verbose("Open API Endpoint:          "+ INSTANCE.openApiEndpoint);
        Logger.INSTANCE.Verbose("Subscription API Endpoint:  "+ INSTANCE.subsApiEndpoint);
        Logger.INSTANCE.Verbose("Transactional API Endpoint: "+ INSTANCE.transOpenApiEndpoint);
        Logger.INSTANCE.Verbose("Event API Endpoint:         "+ INSTANCE.eventApiEndpoint);

        if(integrationKey == null) {
            throw new IllegalArgumentException("Argument null: integrationKey");
        }

        if(context == null) {
            throw new IllegalArgumentException("Argument null: context");
        }

        try {

            INSTANCE._context = context;
            getSubscription();
            setIntegrationKey(integrationKey);
            FirebaseApp.initializeApp(INSTANCE._context);
            syncSubscription();

        } catch (Exception e) {
            Logger.INSTANCE.Error("setConfig: "+ e.getMessage());
        }
    }

    /**
     * Gets Device ID
     * @return String
     */
    public static String getDeviceId() {
        Logger.INSTANCE.Verbose("getDeviceId method is called.");
        Logger.INSTANCE.Debug("getDeviceId: "+ INSTANCE._subscription.getUdid());
        return INSTANCE._subscription.getUdid();
    }

    /**
     * Gets Advertising ID
     * @return String
     */
    public static String getAdvertisingId() {
        Logger.INSTANCE.Verbose("getAdvertisingId method is called.");
        Logger.INSTANCE.Debug("getAdvertisingId: "+ INSTANCE._subscription.getAdid());
        return INSTANCE._subscription.getAdid();
    }

    /**
     * Gets Subscription Token
     * @return String
     */
    public static String getToken() {
        Logger.INSTANCE.Verbose("getToken method is called.");
        Logger.INSTANCE.Debug("getToken: "+ INSTANCE._subscription.getToken());
        return INSTANCE._subscription.getToken();
    }

    /**
     * Gets dEngage User Key
     * @return String
     */
    public static String getContactKey() {
        Logger.INSTANCE.Verbose("getContactKey method is called.");
        Logger.INSTANCE.Debug("getContactKey: "+ INSTANCE._subscription.getContactKey());
        return INSTANCE._subscription.getContactKey();
    }

    /**
     * Set User Push Permission
     * <p>
     * Use to set permission to a user
     * </p>
     * @param permission True/False
     */
    public static void setPermission(Boolean permission) {
        Logger.INSTANCE.Verbose("setPermission method is called");
        try {
            INSTANCE._subscription.setPermission(permission);
            Logger.INSTANCE.Debug("permission: "+ permission);
            saveSubscription();
        } catch (Exception e) {
            Logger.INSTANCE.Error("setPermission: "+ e.getMessage());
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
        Logger.INSTANCE.Verbose("setContactKey method is called");
        try {
            INSTANCE._subscription.setContactKey(contactKey);
            if(TextUtils.isEmpty(contactKey)) throw new IllegalArgumentException("Argument empty: contactKey");
            Logger.INSTANCE.Debug("contactKey: "+ contactKey);
            saveSubscription();
        } catch (Exception e) {
            Logger.INSTANCE.Error("setContactKey: "+ e.getMessage());
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
        Logger.INSTANCE.Verbose("setIntegrationKey method is called");
        try {
            Logger.INSTANCE.Debug("setIntegrationKey: "+ key);
            if(TextUtils.isEmpty(key)) throw new IllegalArgumentException("Argument empty: key");
            INSTANCE._subscription.setIntegrationKey(key);
            saveSubscription();
        } catch (Exception e) {
            Logger.INSTANCE.Error("setIntegrationKey: "+ e.getMessage());
        }
    }

    /**
     * Subscribe User
     * <p>
     * Use to register a user to dEngage. Only required when you perform a manuel GCM registration.
     * </p>
     * @param token  GCM Token
     */
    public static void subscribe(String token) {
        Logger.INSTANCE.Verbose("subscribe(token) method is called");
        try {
            INSTANCE._subscription.setToken(token);
            if(TextUtils.isEmpty(token)) throw new IllegalArgumentException("Argument empty: token");
            saveSubscription();
            Logger.INSTANCE.Debug("subscribe(token): " + token);
            syncSubscription();
        } catch (Exception e) {
            Logger.INSTANCE.Error("subscribe(token): "+ e.getMessage());
        }
    }

    /**
     * Sync user information with dEngage.
     * <p>
     * Use to send the latest information to dEngage. If you set any property or perform a logout, you are advised to call this method.
     * </p>
     */
    public static void syncSubscription() {
        Logger.INSTANCE.Verbose("syncSubscription method is called");
        try {

            getSubscription();

            String token = INSTANCE._subscription.getToken();

            if(TextUtils.isEmpty(token)) {

                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {
                                    Logger.INSTANCE.Verbose("Token onCanceled");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Logger.INSTANCE.Verbose("Token onFailure");
                                    Logger.INSTANCE.Error("Token retrieved: " + e.getMessage());
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    Logger.INSTANCE.Verbose("Token onComplete");
                                    if (!task.isSuccessful()) {
                                        Logger.INSTANCE.Error("Firebase InstanceId Failed: " + task.getException().getMessage());
                                        return;
                                    }
                                    String token = task.getResult().getToken();
                                    Logger.INSTANCE.Debug("Token retrieved: " + token);
                                    if (!TextUtils.isEmpty(token)) {
                                        INSTANCE._subscription.setToken(token);
                                        saveSubscription();
                                    }
                                }
                            });
            }

            AdvertisingIdWorker adIdWorker = new AdvertisingIdWorker(INSTANCE._context);
            adIdWorker.execute();

            Logger.INSTANCE.Debug("syncSubscription: " + INSTANCE._subscription.toJson());

            request.sendRequestAsync(INSTANCE.subsApiEndpoint, Utils.getUserAgent(INSTANCE._context), INSTANCE._subscription, Subscription.class);

        } catch (Exception e) {
            Logger.INSTANCE.Error("syncSubscription: "+ e.getMessage());
        }
    }


    /**
     * Retention service
     * <p>
     * Use to open report when a GCM message is received. Only required when you perform a manuel GCM registration.
     * </p>
     * @param message The message object.
     */
    public static void sendOpenEvent(Message message) {
        Logger.INSTANCE.Verbose("open method is called");
        try {
            getSubscription();

            if(message == null) throw new IllegalArgumentException("Argument null: message");

            Logger.INSTANCE.Debug("MobileManager.open message: " + message.toJson());
            Logger.INSTANCE.Debug("MobileManager.open token: " + INSTANCE._subscription.getToken());

            Open openSignal = new Open();
            openSignal.setIntegrationKey(INSTANCE._subscription.getIntegrationKey());
            openSignal.setMessageId(message.getMessageId());
            openSignal.setMessageDetails(message.getMessageDetails());
            openSignal.setTransactionId(message.getTransactionId());

            if (!TextUtils.isEmpty(message.getTransactionId()))
                request.sendRequestAsync(INSTANCE.transOpenApiEndpoint, Utils.getUserAgent(INSTANCE._context), openSignal, Open.class);
            else
                request.sendRequestAsync(INSTANCE.openApiEndpoint, Utils.getUserAgent(INSTANCE._context), openSignal, Open.class);

        } catch (Exception e) {
            Logger.INSTANCE.Error("open: "+ e.getMessage());
        }
    }

    /**
     * Retention service
     * <p>
     * Use to hit a custom event report.
     * </p>
     * @param tableName The event table name of the schema.
     * @param key Value of the table key.
     * @param data Addinational key-value data.
     */
    public static void sendCustomEvent(String tableName, String key, Map<String,Object> data) {
        Logger.INSTANCE.Verbose("sendCustomEvent method is called");
        try {
            getSubscription();
            Event event = new Event(INSTANCE._subscription.getIntegrationKey(), tableName, key, data);
            Logger.INSTANCE.Debug("sendCustomEvent: " + event.toJson());
            request.sendRequestAsync(INSTANCE.eventApiEndpoint, Utils.getUserAgent(INSTANCE._context), event, Event.class);
        } catch (Exception e) {
            Logger.INSTANCE.Error("sendCustomEvent: "+ e.getMessage());
        }
    }

    /**
     * Retention service
     * <p>
     * Use to hit a device event report.
     * </p>
     * @param tableName The event table name of the schema.
     * @param data Addinational key-value data.
     */
        public static void sendDeviceEvent(String tableName, Map<String, Object> data) {
        Logger.INSTANCE.Verbose("sendDeviceEvent method is called");

        try {
            getSubscription();
            Event event = new Event(INSTANCE._subscription.getIntegrationKey(), tableName, getDeviceId(), data);
            Logger.INSTANCE.Debug("sendDeviceEvent: " + event.toJson());
            request.sendRequestAsync(INSTANCE.eventApiEndpoint, Utils.getUserAgent(INSTANCE._context), event, Event.class);
        } catch (Exception e) {
            Logger.INSTANCE.Error("sendDeviceEvent: "+ e.getMessage());
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
        Logger.INSTANCE.setLogStatus(status);
    }

    private static void getSubscription() {
        Logger.INSTANCE.Verbose("getSubscription method is called");
        try {

            if (Utils.hasPrefString(INSTANCE._context, Constants.SUBSCRIPTION_KEY)) {
                Logger.INSTANCE.Verbose(Constants.SUBSCRIPTION_KEY + " key is not empty. Subscription model is getting from json file.");
                INSTANCE._subscription = new Gson().fromJson(Utils.getPrefString(INSTANCE._context, Constants.SUBSCRIPTION_KEY), Subscription.class);
            } else {
                INSTANCE._subscription = new Subscription();
            }

        } catch (Exception ex) {
            Logger.INSTANCE.Error("Exception on getSubscription: "+ ex.getMessage());
            INSTANCE._subscription = new Subscription();
        }
    }

    private static void saveSubscription() {
        Logger.INSTANCE.Verbose("saveSubscription method is called");
        try {

            INSTANCE._subscription.setUdid(Utils.udid(INSTANCE._context));
            INSTANCE._subscription.setCarrierId(Utils.carrier(INSTANCE._context));
            INSTANCE._subscription.setAppVersion(Utils.appVersion(INSTANCE._context));
            INSTANCE._subscription.setLocal(Utils.local(INSTANCE._context));
            INSTANCE._subscription.setOs(Utils.osType());
            INSTANCE._subscription.setOsVersion(Utils.osVersion());
            INSTANCE._subscription.setSdkVersion(Constants.SDK_VERSION);
            INSTANCE._subscription.setDeviceName(Utils.deviceName());
            INSTANCE._subscription.setDeviceType(Utils.deviceType(INSTANCE._context));

            Utils.savePrefString(INSTANCE._context, Constants.SUBSCRIPTION_KEY, INSTANCE._subscription.toJson());
        } catch (Exception e) {
            Logger.INSTANCE.Error("saveSubscription: "+ e.getMessage());
        }
    }

    private static class AdvertisingIdWorker extends AsyncTask<Void, String, String> {

        private Context _context;

        AdvertisingIdWorker(Context context) {
            _context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            Logger.INSTANCE.Debug("Getting advertising ID");
            AdvertisingIdClient.Info adInfo = null;
            String advertisingId = "";
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(_context);
                if (!adInfo.isLimitAdTrackingEnabled())
                    advertisingId = adInfo.getId();
            } catch (GooglePlayServicesNotAvailableException e) {
                Logger.INSTANCE.Error("GooglePlayServicesNotAvailableException: "+ e.getMessage());
            } catch (GooglePlayServicesRepairableException e) {
                Logger.INSTANCE.Error("GooglePlayServicesRepairableException: "+ e.getMessage());
            } catch (Exception e) {
                Logger.INSTANCE.Error("Exception: "+e.getMessage());
            }
            return advertisingId;
        }

        @Override
        protected void onPostExecute(String adId) {
            INSTANCE._subscription.setAdid(adId);
            saveSubscription();
        }
    }


}


