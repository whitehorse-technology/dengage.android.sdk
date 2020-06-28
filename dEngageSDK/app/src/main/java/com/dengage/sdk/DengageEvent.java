package com.dengage.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.dengage.sdk.models.Event;
import com.dengage.sdk.models.Session;
import com.dengage.sdk.models.Subscription;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class DengageEvent {

    @SuppressLint("StaticFieldLeak")
    private static DengageEvent _instance = null;
    private Logger logger = Logger.getInstance();
    private Context _context;
    private boolean sessionStarted = false;

    private DengageEvent(Context context) {
        this._context = context;
    }

    public static DengageEvent getInstance(Context context, Intent intent) {

        if(_instance == null) _instance = new DengageEvent(context);

        String referrer = "";

        if(intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null)
                referrer = extras.getString("targetUrl");
        }

        _instance.sessionStart(referrer);

        return _instance;
    }

    public static DengageEvent getInstance(Context context) {
        return getInstance(context, null);
    }

    public DengageEvent setLogStatus(Boolean status) {
        logger.setLogStatus(status);
        return _instance;
    }

    public void sessionStart(String referer) {
        if(sessionStarted) return;
        try {

            // UTM parsing
            HashMap<String, Object> data = new HashMap<>();
            data.put("referer", referer);
            data.put("utm_source", "");
            data.put("utm_medium", "");
            data.put("utm_campaign", "");
            data.put("utm_content", "");
            data.put("utm_term", "");

            sendDeviceEvent("session_info", data);
            sessionStarted = true;
        } catch (Exception ignored) {  }
    }

    public void pageView(Map<String, Object> data) {
        try {
            if (!data.containsKey("page_type"))
                throw new Exception("data must have a valid page_type parameter.");

            sendDeviceEvent("page_view_events", data);
        } catch (Exception ex) { logger.Error(ex.getMessage()); }
    }

    public void sendCartEvents(Map<String, Object> data, String eventType) {
        try {
            Map<String, Object> copyData = new HashMap<>(data);
            data.remove("cartItems");
            String eventId = UUID.randomUUID().toString();

            if (!data.containsKey("event_type"))
                data.remove("event_type");
            if (!data.containsKey("event_id"))
                data.remove("event_id");

            data.put("event_type", eventType);
            data.put("event_id", eventId);

            sendDeviceEvent("shopping_cart_events", data);
            if(copyData.containsKey("cartItems")) {
                Object[] items = (Object[])copyData.get("cartItems");
                for (Object obj : items) {
                    if(obj instanceof HashMap) {
                        HashMap<String, Object> product = (HashMap<String, Object>)obj;
                        product.put("event_id", eventId);
                        sendDeviceEvent("shopping_cart_events_detail", product);
                    }

                }
            }

        } catch(Exception ex) {
            logger.Error(ex.getMessage());
        }
    }

    public void addToCart(Map<String, Object> data) { sendCartEvents(data, "add_to_cart"); }

    public void removeFromCart(Map<String, Object> data) { sendCartEvents(data, "remove_from_cart"); }

    public void viewCart(Map<String, Object> data) { sendCartEvents(data, "view_cart"); }

    public void beginCheckout(Map<String, Object> data) { sendCartEvents(data, "begin_checkout"); }

    public void order(Map<String, Object> data) {
        Map<String, Object> copyData = new HashMap<>(data);
        data.remove("cartItems");
        sendDeviceEvent("order_events", data);

        String eventId = UUID.randomUUID().toString();
        HashMap<String, Object> cartEventParams = new HashMap<>();
        cartEventParams.put("event_type",  "order");
        cartEventParams.put("event_id",  eventId);
        sendDeviceEvent("shopping_cart_events", cartEventParams);

        if(copyData.containsKey("cartItems") && copyData.containsKey("order_id")) {
            Object[] items = (Object[])copyData.get("cartItems");
            for (Object obj : items) {
                if(obj instanceof HashMap) {
                    HashMap<String, Object> product = (HashMap<String, Object>) obj;
                    product.put("order_id", copyData.get("order_id").toString());
                    sendDeviceEvent("order_events_details", product);
                }
            }
        }
    }

    public void search(Map<String, Object> data) { sendDeviceEvent("search_events", data); }

    public void sendWishListEvents(Map<String, Object> data, String eventType) {
        try {
            Map<String, Object> copyData = new HashMap<>(data);
            data.remove("cartItems");
            String eventId = UUID.randomUUID().toString();
            data.put("event_type", eventType);
            data.put("event_id", eventId);
            sendDeviceEvent("wishlist_events", data);

            if (copyData.containsKey("cartItems")) {
                Object[] items = (Object[])copyData.get("cartItems");
                for (Object obj : items) {
                    if (obj instanceof HashMap) {
                        HashMap<String, Object> item = (HashMap<String, Object>) obj;
                        item.put("event_id", eventId);
                        sendDeviceEvent("wishlist_events_detail", item);
                    }
                }
            }
        } catch (Exception ex) {
            logger.Error(ex.getMessage());
        }
    }

    public void addToWishList(Map<String, Object> data) { sendWishListEvents(data, "add"); }

    public void removeFromWishList(Map<String, Object> data) { sendWishListEvents(data, "remove"); }

    public void sendCustomEvent(String tableName, String key, Map<String, Object> data) {
        logger.Verbose("sendCustomEvent method is called");
        try {
            Subscription subscription = DengageManager.getInstance(_context).getSubscription();
            String sessionId = Session.getSession().getSessionId();
            data.put("session_id", sessionId);
            Event event = new Event(subscription.getIntegrationKey(), tableName, key, data);
            logger.Debug("sendCustomEvent: " + event.toJson());
            RequestAsync req = new RequestAsync(event);
            req.executeTask();
        } catch (Exception e) {
            logger.Error("sendCustomEvent: "+ e.getMessage());
        }
    }

    public void sendDeviceEvent(String tableName, Map<String, Object> data) {
        logger.Verbose("sendDeviceEvent method is called");
        try {
            Subscription subscription = DengageManager.getInstance(_context).getSubscription();
            String deviceId = subscription.getDeviceId();
            sendCustomEvent(tableName, deviceId, data);
        } catch (Exception e) {
            logger.Error("sendDeviceEvent: "+ e.getMessage());
        }
    }
}

