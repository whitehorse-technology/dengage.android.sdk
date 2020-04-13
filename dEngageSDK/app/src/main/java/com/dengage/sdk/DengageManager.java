package com.dengage.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.NonNull;

import com.dengage.sdk.models.Event;
import com.dengage.sdk.models.Message;
import com.dengage.sdk.models.Open;
import com.dengage.sdk.models.Session;
import com.dengage.sdk.models.Subscription;
import com.dengage.sdk.models.TransactionalOpen;
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
import org.w3c.dom.Text;
import java.util.Map;

public class DengageManager {

    private static Logger logger = Logger.getInstance();

    @SuppressLint("StaticFieldLeak")
    private static DengageManager _instance = null;

    private Context _context;

    private Subscription _subscription;

    private DengageManager(Context context) {
        _context = context;
    }

    /**
     * Singleton Object
     * <p>
     * Use to create dEngage MobileManager.
     * @return DengageManager
     * </p>
     */
    public static DengageManager getInstance(Context context) {

        if(context == null) {
            throw new IllegalArgumentException("Argument null: context");
        }

        if(_instance == null)
            _instance = new DengageManager(context);

        _instance.buildSubscription();

        return _instance;
    }

    /**
     * FirebaseApp Initiator method
     * <p>
     * Use to init Firebase Messaging
     * @return DengageManager
     * </p>
     */
    public DengageManager init() {
        try {
            if(_context == null) throw new Exception("_context is null.");

            FirebaseApp.initializeApp(_context);

            FirebaseTokenWorker tokenWorker = new FirebaseTokenWorker();
            tokenWorker.execute();

            AdvertisingIdWorker adIdWorker = new AdvertisingIdWorker();
            adIdWorker.executeTask();

        } catch (Exception e) {
            logger.Error("initialization:" + e.getMessage());
        }
        return _instance;
    }

    /**
     * Set User Push Permission
     * <p>
     * Use to set permission to a user
     * </p>
     * @param permission True/False
     */
    public void setPermission(Boolean permission) {
        logger.Verbose("setPermission method is called");
        try {
            _subscription.setPermission(permission);
            logger.Debug("permission: "+ permission);
            saveSubscription();
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
    public void setContactKey(String contactKey) {
        logger.Verbose("setContactKey method is called");
        try {
            _subscription.setContactKey(contactKey);
            logger.Debug("contactKey: "+ contactKey);
            saveSubscription();
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
    public DengageManager setIntegrationKey(String key) {
        logger.Verbose("setIntegrationKey method is called");

        if(key == null || TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Argument null: key");
        }

        try {
            logger.Debug("setIntegrationKey: "+ key);
            _subscription.setIntegrationKey(key);
            saveSubscription();
        } catch (Exception e) {
            logger.Error("setIntegrationKey: "+ e.getMessage());
        }
        return _instance;
    }

    /**
     * Set Test Group
     * <p>
     * Use to set testGroup alias at runtime.
     * </p>
     * @param testGroup Test group alias
     */
    public DengageManager setTestGroup(String testGroup) {
        logger.Verbose("setTestGroup method is called");

        try {
            logger.Debug("setIntegrationKey: "+ testGroup);
            _subscription.setTestGroup(testGroup);
            saveSubscription();
        } catch (Exception e) {
            logger.Error("setIntegrationKey: "+ e.getMessage());
        }
        return _instance;
    }

    public DengageManager useCloudSubscription(boolean cloudSubscription) {
        logger.Verbose("useCloudSubscription method is called");

        try {
            logger.Debug("useCloudSubscription: "+ cloudSubscription);
            _subscription.setCloudSubscription(cloudSubscription);
            saveSubscription();
        } catch (Exception e) {
            logger.Error("useCloudSubscription: "+ e.getMessage());
        }
        return _instance;
    }

    /**
     * Subscribe User
     * <p>
     * Use to register a user to dEngage. Only required when you perform a manuel GCM registration.
     * </p>
     * @param token GCM Token
     */
    public void subscribe(String token) {
        logger.Verbose("subscribe(token) method is called");
        try {
            if(TextUtils.isEmpty(token))
                throw new IllegalArgumentException("Argument empty: token");
            _subscription.setToken(token);
            saveSubscription();
            syncSubscription();
        } catch (Exception e) {
            logger.Error("subscribe(token): "+ e.getMessage());
        }
    }

    /**
     *
     * @return Subscription Object from the saved json.
     */
    public Subscription getSubscription() {
        return _subscription;
    }

    private void buildSubscription() {
        try {
            if (Utils.hasSubscription(_context)) {
                 _subscription = new Gson().fromJson(Utils.getSubscription(_context), Subscription.class);
            } else {
                _subscription = new Subscription();
            }
        } catch (Exception ex) {
            logger.Error("buildSubscription: "+ ex.getMessage());
            _subscription = new Subscription();
        }
    }

    /**
     * Save subscription json to disk.
     */
    private void saveSubscription() {
        logger.Verbose("saveSubscription method is called");
        try {

            _subscription.setDeviceId(Utils.getDeviceId(_context));
            _subscription.setCarrierId(Utils.carrier(_context));
            _subscription.setAppVersion(Utils.appVersion(_context));
            _subscription.setSdkVersion(com.dengage.sdk.BuildConfig.VERSION_NAME);
            _subscription.setUserAgent(Utils.getUserAgent(_context));

            String json = _subscription.toJson();
            logger.Debug("subscriptionJson: "+ json);
            Utils.saveSubscription(_context, json);

        } catch (Exception e) {
            logger.Error("saveSubscription: "+ e.getMessage());
        }
    }

    /**
     * Sync user information with dEngage.
     * <p>
     * Use to send the latest information to dEngage. If you set any property or perform a logout, you are advised to call this method.
     * </p>
     */
    public void syncSubscription() {
        logger.Verbose("syncSubscription method is called");
        try {
            boolean cloudSubscription = _subscription.getCloudSubscription();
            if(cloudSubscription) {
                DengageEvent.getInstance(_context, null).subscription();
            } else {
                RequestAsync req = new RequestAsync(_subscription);
                req.executeTask();
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
    public void sendOpenEvent(Message message) {
        logger.Verbose("sendOpenEvent method is called");
        try {
            getSubscription();

            if(message == null) throw new IllegalArgumentException("Argument null: message");

            String source = message.getMessageSource();
            if (!Constants.MESSAGE_SOURCE.equals(source))  return;

            if (!TextUtils.isEmpty(message.getTransactionId())) {
                TransactionalOpen openSignal = new TransactionalOpen();
                openSignal.setUserAgent(Utils.getUserAgent(_context));
                openSignal.setIntegrationKey(_subscription.getIntegrationKey());
                openSignal.setMessageId(message.getMessageId());
                openSignal.setTransactionId(message.getTransactionId());
                openSignal.setMessageDetails(message.getMessageDetails());
                RequestAsync req = new RequestAsync(openSignal);
                req.executeTask();
            } else {
                Open openSignal = new Open();
                openSignal.setUserAgent(Utils.getUserAgent(_context));
                openSignal.setIntegrationKey(_subscription.getIntegrationKey());
                openSignal.setMessageId(message.getMessageId());
                openSignal.setMessageDetails(message.getMessageDetails());
                RequestAsync req = new RequestAsync(openSignal);
                req.executeTask();
            }

        } catch (Exception e) {
            logger.Error("sendOpenEvent: "+ e.getMessage());
        }
    }

    /**
     * Console Log
     * <p>
     * Use to show logs on console.
     * </p>setLogStatus
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
        * @param tableName The event table name of the schema.
        * @param key Value of the event key.
        * @param data Additional key-value data which is correspond table column name-value.
     */
    public void sendCustomEvent(String tableName, String key, Map<String,Object> data) {
        logger.Verbose("sendCustomEvent method is called");
        try {
            getSubscription();
            Event event = new Event(_subscription.getIntegrationKey(), tableName, key, data);
            logger.Debug("sendCustomEvent: " + event.toJson());
            RequestAsync req = new RequestAsync(event);
            req.executeTask();
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
    public void sendDeviceEvent(String tableName, Map<String, Object> data) {
        logger.Verbose("sendDeviceEvent method is called");
        try {
            getSubscription();
            Event event = new Event(_subscription.getIntegrationKey(), tableName, _subscription.getDeviceId(), data);
            logger.Debug("sendDeviceEvent: " + event.toJson());
            RequestAsync req = new RequestAsync(event);
            req.executeTask();
        } catch (Exception e) {
            logger.Error("sendDeviceEvent: "+ e.getMessage());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AdvertisingIdWorker extends AsyncTask<Void, String, String> {
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
                logger.Error("AdvertisingIdWorker Exception: "+e.getMessage());
            }
            return advertisingId;
        }

        @Override
        protected void onPostExecute(String adId) {
            if(adId != null && !TextUtils.isEmpty(adId)) {
                _subscription.setAdvertisingId(adId);
                saveSubscription();
            }
        }

        public void executeTask() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                this.execute();
        }
    }

    private class FirebaseTokenWorker  {

        void execute() {

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

                    if(task.getResult() != null) {
                        String token = task.getResult().getToken();
                        logger.Debug("Token retrieved: " + token);
                        _subscription.setToken(token);
                        saveSubscription();
                    }
                }
            });

        }
    }
}


