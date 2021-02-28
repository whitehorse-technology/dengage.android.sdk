package com.dengage.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dengage.sdk.cache.Prefs;
import com.dengage.sdk.callback.DengageCallback;
import com.dengage.sdk.inappmessage.InAppMessageManager;
import com.dengage.sdk.models.DengageError;
import com.dengage.sdk.models.InboxMessage;
import com.dengage.sdk.models.Message;
import com.dengage.sdk.models.Open;
import com.dengage.sdk.models.SdkParameters;
import com.dengage.sdk.models.Subscription;
import com.dengage.sdk.models.TransactionalOpen;
import com.dengage.sdk.service.NetworkRequest;
import com.dengage.sdk.service.NetworkRequestCallback;
import com.dengage.sdk.service.NetworkUrlUtils;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.api.HuaweiApiAvailability;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

public class DengageManager {

    private static final Logger logger = Logger.getInstance();
    @SuppressLint("StaticFieldLeak")
    private static DengageManager _instance = null;
    private final Context _context;
    private final Prefs prefs;
    private Subscription _subscription;
    private boolean isSubscriptionSending = false;

    private List<InboxMessage> inboxMessages = new ArrayList<>();
    private Long inboxMessageFetchMillis = 0L;

    private InAppMessageManager inAppMessageManager;

    private DengageManager(Context context) {
        _context = context;
        prefs = new Prefs(context);
    }

    /**
     * Singleton Object
     * <p>
     * Use to create dEngage MobileManager.
     *
     * @return DengageManager
     * </p>
     */
    public static DengageManager getInstance(Context context) {

        if (context == null) {
            throw new IllegalArgumentException("Argument null: context");
        }

        if (_instance == null)
            _instance = new DengageManager(context);

        _instance.buildSubscription();

        return _instance;
    }

    public DengageManager setDeviceId(String deviceId) {
        logger.Verbose("setDeviceId method is called");
        try {
            // control the last device id equals to new device id then send subscription
            if (_subscription.getDeviceId() == null || !_subscription.getDeviceId().equals(deviceId)) {
                _subscription.setDeviceId(deviceId);
                logger.Debug("deviceId: " + deviceId);
                saveSubscription();
                sendSubscription();
            }
        } catch (Exception e) {
            logger.Error("setDeviceId: " + e.getMessage());
        }
        return _instance;
    }

    public DengageManager setCountry(String country) {
        logger.Verbose("setCountry method is called");
        try {
            // control the last country equals to new country then send subscription
            if (_subscription.getCountry() == null || !_subscription.getCountry().equals(country)) {
                _subscription.setCountry(country);
                logger.Debug("country: " + country);
                saveSubscription();
                sendSubscription();
            }
        } catch (Exception e) {
            logger.Error("setCountry: " + e.getMessage());
        }
        return _instance;
    }

    /**
     * FirebaseApp Initiator method
     * <p>
     * Use to init Firebase Messaging
     *
     * @return DengageManager
     * </p>
     */
    public DengageManager init() {
        try {
            // create in app message manager and start new session
            inAppMessageManager = new InAppMessageManager(_context, _subscription, logger);
            inAppMessageManager.startNewSession();

            if (isGooglePlayServicesAvailable() && isHuaweiMobileServicesAvailable()) {
                logger.Verbose("Google Play Services and Huawei Mobile Service are available. Firebase services will be used.");
                initFirebase();
            } else if (isHuaweiMobileServicesAvailable()) {
                logger.Verbose("Huawei Mobile Services is available.");
                initHuawei();
            } else if (isGooglePlayServicesAvailable()) {
                initFirebase();
            }

            sendSubscription();
            getSdkParameters();
        } catch (Exception e) {
            logger.Error("initialization:" + e.getMessage());
        }
        return _instance;
    }

    public boolean isGooglePlayServicesAvailable() {
        try {
            Class.forName("com.google.android.gms.common.GoogleApiAvailability");
            return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(_context) == com.google.android.gms.common.ConnectionResult.SUCCESS;
        } catch (Exception ignored) {
            return false;
        }
    }

    public boolean isHuaweiMobileServicesAvailable() {
        try {
            Class.forName("com.huawei.hms.api.HuaweiApiAvailability");
            return HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(_context) == com.huawei.hms.api.ConnectionResult.SUCCESS;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void initHuawei() {
        _subscription.setTokenType(Constants.HUAWEI_TOKEN_TYPE);
        _subscription.setIntegrationKey(_subscription.getHuaweiIntegrationKey());
        saveSubscription();
        HmsTokenWorker hmsTokenWorker = new HmsTokenWorker();
        hmsTokenWorker.executeTask();
        HmsAdIdWorker hmsAdIdWorker = new HmsAdIdWorker();
        hmsAdIdWorker.executeTask();
    }

    private void initFirebase() {
        _subscription.setTokenType(Constants.FIREBASE_TOKEN_TYPE);
        _subscription.setIntegrationKey(_subscription.getFirebaseIntegrationKey());
        saveSubscription();
        FirebaseApp.initializeApp(_context);
        GmsTokenWorker gmsTokenWorker = new GmsTokenWorker();
        gmsTokenWorker.executeTask();
        GmsAdIdWorker gmsAdIdWorker = new GmsAdIdWorker();
        gmsAdIdWorker.executeTask();
    }

    /**
     * Set User Push Permission
     * <p>
     * Use to set permission to a user
     * </p>
     *
     * @param permission True/False
     */
    public void setPermission(Boolean permission) {
        logger.Verbose("setPermission method is called");
        try {
            // control the last permission flag equals to new permission flag then send subscription
            if (_subscription.getPermission() == null || _subscription.getPermission() != permission) {
                _subscription.setPermission(permission);
                logger.Debug("permission: " + permission);
                saveSubscription();
                sendSubscription();
            }
        } catch (Exception e) {
            logger.Error("setPermission: " + e.getMessage());
        }
    }

    /**
     * Set contact key of the user.
     * <p>
     * Use to set dEngage key to a user.
     * </p>
     *
     * @param contactKey user key
     */
    public void setContactKey(String contactKey) {
        logger.Verbose("setContactKey method is called");
        try {
            // control the last contact key equals to new contact key then send subscription
            if (_subscription.getContactKey() == null || !_subscription.getContactKey().equals(contactKey)) {
                _subscription.setContactKey(contactKey);
                logger.Debug("contactKey: " + contactKey);
                saveSubscription();
                sendSubscription();
            }
        } catch (Exception e) {
            logger.Error("setContactKey: " + e.getMessage());
        }
    }


    public DengageManager setFirebaseIntegrationKey(String key) {
        logger.Verbose("setFirebaseIntegrationKey method is called");

        if (key == null || TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Argument null: key");
        }

        try {
            logger.Debug("setFirebaseIntegrationKey: " + key);
            _subscription.setFirebaseIntegrationKey(key);
        } catch (Exception e) {
            logger.Error("setFirebaseIntegrationKey: " + e.getMessage());
        }
        return _instance;
    }

    public DengageManager setHuaweiIntegrationKey(String key) {
        logger.Verbose("setHuaweiIntegrationKey method is called");

        if (key == null || TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Argument null: key");
        }

        try {
            logger.Debug("setHuaweiIntegrationKey: " + key);
            _subscription.setHuaweiIntegrationKey(key);
        } catch (Exception e) {
            logger.Error("setHuaweiIntegrationKey: " + e.getMessage());
        }
        return _instance;
    }

    /**
     * Subscribe User
     * <p>
     * Use to register a user to dEngage. Only required when you perform a manuel GCM registration.
     * </p>
     *
     * @param token GCM Token
     */
    public void subscribe(String token) {
        logger.Verbose("subscribe(token) method is called");
        try {
            if (TextUtils.isEmpty(token))
                throw new IllegalArgumentException("Argument empty: token");
            _subscription.setToken(token);
            saveSubscription();
            sendSubscription();
        } catch (Exception e) {
            logger.Error("subscribe(token): " + e.getMessage());
        }
    }

    /**
     * @return Subscription Object from the saved json.
     */
    public Subscription getSubscription() {
        return _subscription;
    }


    public void buildSubscription() {
        try {
            if (Utils.hasSubscription(_context)) {
                _subscription = new Gson().fromJson(Utils.getSubscription(_context), Subscription.class);
            } else {
                _subscription = new Subscription();
            }
        } catch (Exception ex) {
            logger.Error("buildSubscription: " + ex.getMessage());
            _subscription = new Subscription();
        }
    }

    public void saveSubscription() {
        logger.Verbose("saveSubscription method is called");
        try {

            if (TextUtils.isEmpty(_subscription.getDeviceId()))
                _subscription.setDeviceId(Utils.getDeviceId(_context));
            _subscription.setCarrierId(Utils.carrier(_context));
            _subscription.setAppVersion(Utils.appVersion(_context));
            _subscription.setSdkVersion(Utils.getSdkVersion());
            _subscription.setUserAgent(Utils.getUserAgent(_context));
            _subscription.setLanguage(Locale.getDefault().getLanguage());
            _subscription.setTimezone(TimeZone.getDefault().getDisplayName());

            String json = _subscription.toJson();
            logger.Debug("subscriptionJson: " + json);
            Utils.saveSubscription(_context, json);

        } catch (Exception e) {
            logger.Error("saveSubscription: " + e.getMessage());
        }
    }

    private void sendSubscription() {
        logger.Verbose("sendSubscription method is called");
        if (isSubscriptionSending) return;
        try {
            isSubscriptionSending = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        String baseApiUri = Utils.getMetaData(_context, "den_push_api_url");
                        if (TextUtils.isEmpty(baseApiUri))
                            baseApiUri = Constants.DEN_PUSH_API_URI;
                        baseApiUri += Constants.SUBSCRIPTION_API_ENDPOINT;
                        RequestAsync req = new RequestAsync(baseApiUri, _subscription);
                        req.executeTask();
                        isSubscriptionSending = false;
                    } catch (Exception e) {
                        isSubscriptionSending = false;
                        logger.Error("sendSubscriptionDelay: " + e.getMessage());
                    }
                }
            }, 1500);
        } catch (Exception e) {
            isSubscriptionSending = false;
            logger.Error("sendSubscription: " + e.getMessage());
        }
    }


    /**
     * Deprecated function Subscription will send after changing contact key, permission or device
     * id automatically
     */
    @Deprecated
    public void syncSubscription() {

    }

    /**
     * Sends open event
     * <p>
     * Use to open report when a GCM message is received. Only required when you perform a manuel
     * GCM registration.
     * </p>
     *
     * @param message The dEngage message object.
     */
    public void sendOpenEvent(String buttonId, String itemId, Message message) {
        logger.Verbose("sendOpenEvent method is called");
        logger.Verbose(buttonId);
        logger.Verbose(itemId);
        logger.Verbose(message.toJson());
        try {
            getSubscription();

            if (message == null) throw new IllegalArgumentException("Argument null: message");

            String source = message.getMessageSource();
            if (!Constants.MESSAGE_SOURCE.equals(source)) return;

            if (!TextUtils.isEmpty(message.getTransactionId())) {

                String baseApiUri = Utils.getMetaData(_context, "den_push_api_url");
                if (TextUtils.isEmpty(baseApiUri))
                    baseApiUri = Constants.DEN_PUSH_API_URI;

                baseApiUri += Constants.TRANSACTIONAL_OPEN_API_ENDPOINT;

                TransactionalOpen openSignal = new TransactionalOpen();
                openSignal.setUserAgent(Utils.getUserAgent(_context));
                openSignal.setIntegrationKey(_subscription.getIntegrationKey());
                openSignal.setMessageId(message.getMessageId());
                openSignal.setTransactionId(message.getTransactionId());
                openSignal.setMessageDetails(message.getMessageDetails());
                openSignal.setButtonId(buttonId);
                openSignal.setItemId(itemId);
                RequestAsync req = new RequestAsync(baseApiUri, openSignal);
                req.executeTask();
            } else {

                String baseApiUri = Utils.getMetaData(_context, "den_push_api_url");
                if (TextUtils.isEmpty(baseApiUri))
                    baseApiUri = Constants.DEN_PUSH_API_URI;

                baseApiUri += Constants.OPEN_API_ENDPOINT;

                Open openSignal = new Open();
                openSignal.setUserAgent(Utils.getUserAgent(_context));
                openSignal.setIntegrationKey(_subscription.getIntegrationKey());
                openSignal.setMessageId(message.getMessageId());
                openSignal.setMessageDetails(message.getMessageDetails());
                openSignal.setButtonId(buttonId);
                openSignal.setItemId(itemId);
                RequestAsync req = new RequestAsync(baseApiUri, openSignal);
                req.executeTask();

                // send session start
                DengageEvent.getInstance(this._context, message.getTargetUrl());
            }

        } catch (Exception e) {
            logger.Error("sendOpenEvent: " + e.getMessage());
        }
    }

    /**
     * Console Log
     * <p>
     * Use to show logs on console.
     * </p>setLogStatus
     *
     * @param status True/False
     */
    public DengageManager setLogStatus(Boolean status) {
        logger.setLogStatus(status);
        return _instance;
    }

    /**
     * Sends a custom event
     * <p>
     * Use to hit a custom event report.
     * </p>
     *
     * @param tableName The event table name of the schema.
     * @param key       Value of the event key.
     * @param data      Additional key-value data which is correspond table column name-value.
     */
    public void sendCustomEvent(String tableName, String key, Map<String, Object> data) {
        logger.Verbose("sendCustomEvent method is called");
        try {
            DengageEvent.getInstance(_context).sendCustomEvent(tableName, key, data);
        } catch (Exception e) {
            logger.Error("sendCustomEvent: " + e.getMessage());
        }
    }

    /**
     * Sends a device event
     * <p>
     * Use to hit a device event report.
     * </p>
     *
     * @param tableName The event table name of the schema.
     * @param data      Additional key-value data which is correspond table column name-value.
     */
    public void sendDeviceEvent(String tableName, Map<String, Object> data) {
        logger.Verbose("sendDeviceEvent method is called");
        try {
            DengageEvent.getInstance(_context).sendDeviceEvent(tableName, data);
        } catch (Exception e) {
            logger.Error("sendDeviceEvent: " + e.getMessage());
        }
    }

    private class GmsAdIdWorker extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... params) {
            logger.Debug("Getting GMS advertising ID");
            com.google.android.gms.ads.identifier.AdvertisingIdClient.Info adInfo;
            String advertisingId = "";
            try {
                adInfo = com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo(_context);
                if (!adInfo.isLimitAdTrackingEnabled())
                    advertisingId = adInfo.getId();
            } catch (Exception e) {
                logger.Error("GmsAdIdWorker Exception: " + e.getMessage());
            }
            return advertisingId;
        }

        @Override
        protected void onPostExecute(String adId) {
            logger.Verbose("GmsAdIdWorker executed: " + adId);
            if (adId != null && !TextUtils.isEmpty(adId)) {
                _subscription.setAdvertisingId(adId);
                saveSubscription();
                sendSubscription();
            }
        }

        public void executeTask() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                this.execute();
        }
    }

    private class GmsTokenWorker {

        void executeTask() {
            try {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            logger.Error("Firebase InstanceId Failed: " + task.getException().getMessage());
                            return;
                        }

                        if (task.getResult() != null) {
                            String token = task.getResult().getToken();
                            logger.Debug("GMS Token retrieved: " + token);
                            _subscription.setToken(token);
                            saveSubscription();
                            sendSubscription();
                        }
                    }
                });
            } catch (Exception e) {
                logger.Error("GmsTokenWorker Exception: " + e.getMessage());
            }
        }
    }

    private class HmsAdIdWorker extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... params) {
            logger.Debug("Getting HMS advertising ID");
            String advertisingId = "";
            try {
                com.huawei.hms.ads.identifier.AdvertisingIdClient.Info adInfo
                        = com.huawei.hms.ads.identifier.AdvertisingIdClient.
                        getAdvertisingIdInfo(_context);
                if (!adInfo.isLimitAdTrackingEnabled())
                    advertisingId = adInfo.getId();
            } catch (Exception e) {
                logger.Error("HmsAdIdWorker Exception: " + e.getMessage());
            }
            return advertisingId;
        }

        @Override
        protected void onPostExecute(String adId) {
            if (adId != null && !TextUtils.isEmpty(adId)) {
                _subscription.setAdvertisingId(adId);
                saveSubscription();
                sendSubscription();
            }
        }

        public void executeTask() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                this.execute();
        }
    }

    private class HmsTokenWorker extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... params) {
            logger.Debug("Getting Hms Token");
            String token = "";
            try {
                String appId = AGConnectServicesConfig.fromContext(_context).getString("client/app_id");
                token = HmsInstanceId.getInstance(_context).getToken(appId, com.huawei.hms.push.HmsMessaging.DEFAULT_TOKEN_SCOPE);
            } catch (Exception e) {
                logger.Error("HmsTokenWorker Exception: " + e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            if (token != null && !TextUtils.isEmpty(token)) {
                _subscription.setToken(token);
                saveSubscription();
                sendSubscription();
            }
        }

        public void executeTask() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                this.execute();
        }
    }

    public void onNewToken(String token) {
        try {
            logger.Debug("On new token : " + token);
            if (!TextUtils.isEmpty(token)) {
                logger.Debug("Send subscribe");
                DengageManager.getInstance(_context).subscribe(token);
            }
        } catch (Exception e) {
            logger.Error("onNewToken: " + e.getMessage());
        }
    }

    public void onMessageReceived(Map<String, String> data) {
        logger.Verbose("onMessageReceived method is called.");
        logger.Verbose("Raw Message: " + new JSONObject(data).toString());

        if ((data != null && data.size() > 0)) {
            Message pushMessage = new Message(data);
            String json = pushMessage.toJson();
            logger.Verbose("Message Json: " + json);

            String source = pushMessage.getMessageSource();
            if (Constants.MESSAGE_SOURCE.equals(source)) {
                logger.Debug("There is a message that received from dEngage");
                sendBroadcast(_context, json, data);
            }
        }
    }

    private void sendBroadcast(Context context, String json, Map<String, String> data) {
        logger.Verbose("sendBroadcast method is called.");
        try {
            Intent intent = new Intent(Constants.PUSH_RECEIVE_EVENT);
            intent.putExtra("RAW_DATA", json);
            logger.Verbose("RAW_DATA: " + json);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
            intent.setPackage(context.getPackageName());
            context.sendBroadcast(intent);
        } catch (Exception e) {
            logger.Error("sendBroadcast: " + e.getMessage());
        }
    }

    private void getSdkParameters() {
        if (TextUtils.isEmpty(_subscription.integrationKey)) return;

        // if 24 hours passed after getting sdk params, you should get again
        if (prefs.getSdkParameters() != null &&
                System.currentTimeMillis() < prefs.getSdkParameters().getLastFetchTimeInMillis() + 24 * 60 * 60 * 1000) {
            return;
        }

        NetworkRequest networkRequest = new NetworkRequest(
                NetworkUrlUtils.INSTANCE.getSdkParametersRequestUrl(_context, _subscription.integrationKey),
                Utils.getUserAgent(_context), new NetworkRequestCallback() {
            @Override
            public void responseFetched(@Nullable String response) {
                if (response != null) {
                    SdkParameters sdkParameters = new Gson().fromJson(response, SdkParameters.class);
                    sdkParameters.setLastFetchTimeInMillis(System.currentTimeMillis());
                    prefs.setSdkParameters(sdkParameters);

                    // after fetching sdk parameters, fetch in app messages
                    getInAppMessages();
                }
            }

            @Override
            public void requestError(@NotNull DengageError error) {
                // error behavior
            }
        });
        networkRequest.executeTask();
    }

    public void getInAppMessages() {
        inAppMessageManager.fetchInAppMessages();
    }

    public void setNavigation(@NonNull AppCompatActivity activity, @Nullable String screenName) {
        inAppMessageManager.setNavigation(activity, screenName, null);
    }

    /**
     * Get saved inbox messages
     */
    public void getInboxMessages(@NonNull Integer limit, @NonNull final Integer offset,
                                 @NonNull final DengageCallback<List<InboxMessage>> dengageCallback) {
        // control inbox message enabled
        SdkParameters sdkParameters = prefs.getSdkParameters();
        if (sdkParameters == null || sdkParameters.getAccountName() == null ||
                sdkParameters.getInboxEnabled() == null || !sdkParameters.getInboxEnabled()) {
            dengageCallback.onResult(new ArrayList<InboxMessage>());
            return;
        }

        if (inboxMessages != null && !inboxMessages.isEmpty() && offset == 0 &&
                System.currentTimeMillis() < inboxMessageFetchMillis + 600000) {
            dengageCallback.onResult(inboxMessages);
        } else {
            NetworkRequest networkRequest = new NetworkRequest(
                    NetworkUrlUtils.INSTANCE.getInboxMessagesRequestUrl(_context,
                            sdkParameters.getAccountName(), _subscription, limit, offset),
                    Utils.getUserAgent(_context), new NetworkRequestCallback() {
                @Override
                public void responseFetched(@Nullable String response) {
                    inboxMessageFetchMillis = System.currentTimeMillis();
                    Type listType = new TypeToken<List<InboxMessage>>() {
                    }.getType();
                    List<InboxMessage> fetchedInboxMessages = new Gson().fromJson(response, listType);
                    if (offset == 0) {
                        inboxMessages = fetchedInboxMessages;
                    }
                    dengageCallback.onResult(fetchedInboxMessages);
                }

                @Override
                public void requestError(@NotNull DengageError error) {
                    dengageCallback.onError(error);
                }
            });
            networkRequest.executeTask();
        }
    }

    /**
     * Delete inbox message
     *
     * @param id id of inbox message that will be deleted.
     */
    public void deleteInboxMessage(final String id) {
        // control inbox message enabled
        SdkParameters sdkParameters = prefs.getSdkParameters();
        if (sdkParameters == null || sdkParameters.getAccountName() == null ||
                sdkParameters.getInboxEnabled() == null || !sdkParameters.getInboxEnabled()) {
            return;
        }

        // remove cached inbox message with id
        CollectionsKt.removeAll(inboxMessages, new Function1<InboxMessage, Boolean>() {
            @Override
            public Boolean invoke(InboxMessage inboxMessage) {
                inboxMessage.getId();
                return inboxMessage.getId().equals(id);
            }
        });

        // call http request
        NetworkRequest networkRequest = new NetworkRequest(
                NetworkUrlUtils.INSTANCE.setInboxMessageAsDeletedRequestUrl(_context, id,
                        sdkParameters.getAccountName(), _subscription),
                Utils.getUserAgent(_context), null);
        networkRequest.executeTask();
    }

    /**
     * Mark inbox message as read
     *
     * @param id id of inbox message that will be marked as read.
     */
    public void setInboxMessageAsClicked(final String id) {
        // control inbox message enabled
        SdkParameters sdkParameters = prefs.getSdkParameters();
        if (sdkParameters == null || sdkParameters.getAccountName() == null ||
                sdkParameters.getInboxEnabled() == null || !sdkParameters.getInboxEnabled()) {
            return;
        }

        // find cached inbox message with id and set clicked
        InboxMessage inboxMessage = CollectionsKt.firstOrNull(inboxMessages, new Function1<InboxMessage, Boolean>() {
            @Override
            public Boolean invoke(InboxMessage inboxMessage) {
                inboxMessage.getId();
                return inboxMessage.getId().equals(id);
            }
        });
        if (inboxMessage != null) {
            inboxMessage.setClicked(true);
        }

        // call http request
        NetworkRequest networkRequest = new NetworkRequest(
                NetworkUrlUtils.INSTANCE.setInboxMessageAsClickedRequestUrl(_context, id,
                        sdkParameters.getAccountName(), _subscription),
                Utils.getUserAgent(_context), null);
        networkRequest.executeTask();
    }
}


