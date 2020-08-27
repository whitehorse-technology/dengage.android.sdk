package com.dengage.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
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

    public static DengageEvent getInstance(Context context, String referer) {

        if(_instance == null) _instance = new DengageEvent(context);

        _instance.sessionStart(referer);

        return _instance;
    }

    public static DengageEvent getInstance(Context context) {
        return getInstance(context, "");
    }

    public DengageEvent setLogStatus(Boolean status) {
        logger.setLogStatus(status);
        return _instance;
    }

    public void sessionStart(String referer) {
        if(sessionStarted) return;
        try {

            HashMap<String, Object> data = new HashMap<>();
            data.put("referer", referer);

            try {
                Uri uri = Uri.parse(referer);
                if (uri.getQueryParameter("utm_source") != null)
                    data.put("utm_source", uri.getQueryParameter("utm_source"));
                if (uri.getQueryParameter("utm_medium") != null)
                    data.put("utm_medium", uri.getQueryParameter("utm_medium"));
                if (uri.getQueryParameter("utm_campaign") != null)
                    data.put("utm_campaign", uri.getQueryParameter("utm_campaign"));
                if (uri.getQueryParameter("utm_content") != null)
                    data.put("utm_content", uri.getQueryParameter("utm_content"));
                if (uri.getQueryParameter("utm_term") != null)
                    data.put("utm_term", uri.getQueryParameter("utm_term"));
                if (uri.getQueryParameter("gclid") != null)
                    data.put("gclid", uri.getQueryParameter("gclid"));
                if (uri.getQueryParameter("dn_channel") != null)
                    data.put("channel", uri.getQueryParameter("dn_channel"));
                if (uri.getQueryParameter("dn_channel") != null)
                    data.put("send_id", uri.getQueryParameter("dn_send_id"));
                if (uri.getQueryParameter("dn_camp_id") != null)
                    data.put("camp_id", uri.getQueryParameter("dn_camp_id"));

            } catch (Exception ignored) { }

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
            copyData.remove("cartItems");
            String eventId = UUID.randomUUID().toString();

            if (!copyData.containsKey("event_type"))
                copyData.remove("event_type");
            if (!copyData.containsKey("event_id"))
                copyData.remove("event_id");

            copyData.put("event_type", eventType);
            copyData.put("event_id", eventId);

            sendDeviceEvent("shopping_cart_events", copyData);

            if(data.containsKey("cartItems")) {
                Object[] items = (Object[])data.get("cartItems");
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

    public void cancelOrder(Map<String, Object> data) {
        Map<String, Object> copyData = new HashMap<>(data);
        copyData.remove("cartItems");

        if(copyData.containsKey("event_type"))
            copyData.remove("event_type");

        copyData.put("event_type",  "cancel");

        if(copyData.containsKey("total_amount"))
            copyData.put("total_amount",  0);

        sendDeviceEvent("order_events", copyData);

        if(data.containsKey("cartItems")) {
            Object[] items = (Object[])data.get("cartItems");
            for (Object obj : items) {
                if(obj instanceof HashMap) {
                    HashMap<String, Object> product = (HashMap<String, Object>) obj;
                    if(copyData.containsKey("order_id"))
                        product.put("order_id", copyData.get("order_id").toString());
                    sendDeviceEvent("order_events_detail", product);
                }
            }
        }
    }

    public void order(Map<String, Object> data) {
        Map<String, Object> copyData = new HashMap<>(data);
        copyData.remove("cartItems");

        if(!copyData.containsKey("event_type"))
            copyData.remove("event_type");
        copyData.put("event_type",  "order");

        sendDeviceEvent("order_events", copyData);

        String eventId = UUID.randomUUID().toString();
        HashMap<String, Object> cartEventParams = new HashMap<>();
        cartEventParams.put("event_type",  "order");
        cartEventParams.put("event_id",  eventId);
        sendDeviceEvent("shopping_cart_events", cartEventParams);

        if(data.containsKey("cartItems")) {
            Object[] items = (Object[])data.get("cartItems");
            for (Object obj : items) {
                if(obj instanceof HashMap) {
                    HashMap<String, Object> product = (HashMap<String, Object>) obj;
                    if(copyData.containsKey("order_id"))
                        product.put("order_id", copyData.get("order_id").toString());
                    sendDeviceEvent("order_events_detail", product);
                }
            }
        }
    }

    public void search(Map<String, Object> data) { sendDeviceEvent("search_events", data); }

    public void sendWishListEvents(Map<String, Object> data, String eventType) {
        try {
            Map<String, Object> copyData = new HashMap<>(data);
            copyData.remove("items");
            String eventId = UUID.randomUUID().toString();

            if (!copyData.containsKey("event_type"))
                copyData.remove("event_type");
            if (!copyData.containsKey("event_id"))
                copyData.remove("event_id");

            copyData.put("event_type", eventType);
            copyData.put("event_id", eventId);

            sendDeviceEvent("wishlist_events", copyData);

            if (data.containsKey("items")) {
                Object[] items = (Object[])data.get("items");
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
            String endpoint = Utils.getMetaData(_context, "den_event_api_endpoint");
            if (TextUtils.isEmpty(endpoint))
                endpoint = Constants.EVENT_API_ENDPOINT;
            data.put("session_id", sessionId);
            Event event = new Event(subscription.getIntegrationKey(), tableName, key, data);
            logger.Debug("sendCustomEvent: " + event.toJson());
            RequestAsync req = new RequestAsync(endpoint, event);
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

