package com.dengage.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.dengage.sdk.models.CardItem;
import com.dengage.sdk.models.DenEvent;
import com.dengage.sdk.models.PageType;
import com.dengage.sdk.models.Session;
import com.dengage.sdk.models.Subscription;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if(intent != null) {
            String actionUrl = "";
            Bundle extras = intent.getExtras();
            if (extras != null)
                actionUrl = extras.getString("targetUrl");

            _instance.startSession(actionUrl);
        }

        return _instance;
    }

    public static DengageEvent getInstance(Context context) {
        return getInstance(context, null);
    }

    public DengageEvent setLogStatus(Boolean status) {
        logger.setLogStatus(status);
        return _instance;
    }

    public void subscription() {
        try {
            Subscription subscription = DengageManager.getInstance(_context).getSubscription();
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("advertisingId", subscription.getAdvertisingId());
            extras.put("appVersion", subscription.getAppVersion());
            extras.put("carrierId", subscription.getCarrierId());
            extras.put("udid", subscription.getDeviceId());
            extras.put("permission", subscription.getPermission());
            extras.put("sdkVersion", subscription.getSdkVersion());
            extras.put("token", subscription.getToken());
            extras.put("tokenType", subscription.getTokenType());
            extras.put("trackingPermission", subscription.getTrackingPermission());
            extras.put("webSubscription", subscription.getWebSubscription());
            extras.put("integrationKey", subscription.getIntegrationKey());

            sendEvent("subscription", extras);
        } catch (Exception ignored) { }
    }

    public void startSession(String actionUrl) {
        if(sessionStarted) return;
        try {
            Subscription subscription = DengageManager.getInstance(_context).getSubscription();
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("language", Utils.getSystemLanguage());
            extras.put("platform", "");
            extras.put("screenWidth", Utils.getScreenWith(_context));
            extras.put("screenHeight", Utils.getScreenHeight(_context));
            extras.put("timeZone", Utils.getTimezoneId());
            extras.put("sdkVersion", Utils.getSdkVersion());
            extras.put("referrer", "");
            extras.put("location", actionUrl);
            extras.put("userAgent", Utils.getUserAgent(_context));
            extras.put("token", subscription.getToken());
            extras.put("appVersion", subscription.getAppVersion());
            extras.put("permission", subscription.getPermission());
            extras.put("os", Utils.getOsVersion());
            extras.put("model", Utils.getModel());
            extras.put("manufacturer", Utils.getManufacturer());
            extras.put("brand", Utils.getBrand());
            extras.put("deviceId", Utils.getDeviceUniqueId());
            sendEvent("startSession", extras);
            sessionStarted = true;
        } catch (Exception ignored) {  }
    }

    public void tokenRefresh(String token) {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("token", token);

            sendEvent("sdkTokenAction", extras);
        } catch (Exception ignored) { }
    }

    public void productDetail(String productId, Double price, Double discountedPrice, String currency, String supplierId) {
        logger.Verbose("productDetail method is called.");
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("entityType", "product");
            extras.put("entityId", productId);
            extras.put("price", price);
            extras.put("discountedPrice", discountedPrice);
            extras.put("currency", currency);
            extras.put("supplierId", supplierId);
            extras.put("pageType", "productDetail");

            sendEvent("PV", extras);
        } catch (Exception e) { logger.Error(e.getMessage()); }
    }

    public void promotionPage(String promotionId) {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("entityType", "promotion");
            extras.put("entityId", promotionId);
            extras.put("pageType", "promotionPage");

            sendEvent("PV", extras);
        } catch (Exception ignored) { }
    }

    public void categoryPage(String categoryId, String parentCategory) {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("entityType", "category");
            extras.put("entityId", categoryId);
            extras.put("parentCategory", parentCategory);
            extras.put("pageType", "categoryPage");

            sendEvent("PV", extras);
        } catch (Exception ignored) { }
    }

    public void homePage() {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("pageType", "homePage");

            sendEvent("PV", extras);
        } catch (Exception ignored) { }
    }

    public void searchPage(String keyword, long resultCount) {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("pageType", "searchPage");
            extras.put("resultCount", resultCount);
            extras.put("keyword", keyword);

            sendEvent("PV", extras);
        } catch (Exception ignored) { }
    }

    public void refinement(PageType pageType, Map<String, List<String>> filters, long resultCount) {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("pageType", pageType.toString());
            extras.put("filters", filters);
            extras.put("resultCount", resultCount);
            extras.put("entityType", "products");

            sendEvent("Action", extras);
        } catch (Exception ignored) { }
    }

    public void loginPage() {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("pageType", "loginPage");

            sendEvent("PV", extras);
        } catch (Exception ignored) { }
    }

    public void loginAction(String memberId, boolean success, String origin) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("eventType", "loginAction");
        extras.put("origin", origin);
        extras.put("success", success);
        extras.put("memberId", memberId);

        sendEvent("Action", extras);
    }

    public void registerPage() {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("pageType", "registerPage");

            sendEvent("PV", extras);
        } catch (Exception ignored) { }
    }

    public void registerAction(String memberId, boolean success, String origin) {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("eventType", "registerAction");
            extras.put("origin", origin);
            extras.put("success", success);
            extras.put("memberId", memberId);

            sendEvent("Action", extras);
        } catch (Exception ignored) { }
    }

    public void addToBasket(CardItem item, String origin, String basketId) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("eventType", "addToBasket");
        extras.put("origin", origin);
        extras.put("basketId", basketId);
        extras.put("currency", item.getCurrency());
        extras.put("price", item.getPrice());
        extras.put("discountedPrice", item.getDiscountedPrice());
        extras.put("quantity", item.getQuantity());
        extras.put("variantId", item.getVariantId());
        extras.put("productId", item.getProductId());

        sendEvent("Action", extras);
    }

    public void removeFromBasket(String productId, String variantId, int quantity, String basketId) {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("eventType", "removeFromBasket");
            extras.put("productId", productId);
            extras.put("variantId", variantId);
            extras.put("quantity", quantity);
            extras.put("basketId", basketId);

            sendEvent("Action", extras);
        } catch (Exception ignored) { }
    }

    public void basketPage(CardItem[] items, Double totalPrice, String basketId) {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("pageType", "basketPage");
            extras.put("basketId", basketId);
            extras.put("totalPrice", totalPrice);

            StringBuilder productIds = new StringBuilder();
            StringBuilder quantities = new StringBuilder();
            StringBuilder prices = new StringBuilder();
            StringBuilder currencies = new StringBuilder();
            StringBuilder variantIds = new StringBuilder();

            if(items != null && items.length > 0) {
                for (CardItem item:items) {
                    productIds.append(item.getProductId()).append("|");
                    quantities.append(item.getQuantity()).append("|");
                    prices.append(item.getPrice()).append("|");
                    currencies.append(item.getCurrency()).append("|");
                    variantIds.append(item.getVariantId()).append("|");
                }
            }

            extras.put("productIds", productIds.toString());
            extras.put("quantities", quantities.toString());
            extras.put("prices", prices.toString());
            extras.put("currencies", currencies.toString());
            extras.put("variantIds", variantIds.toString());

            sendEvent("PV", extras);
        } catch (Exception ignored) { }
    }

    public void orderSummary(CardItem[] items, Double totalPrice, String basketId, String orderId, String paymentMethod) {
        try {
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("pageType", "orderSummary");
            extras.put("totalPrice", totalPrice);
            extras.put("basketId", basketId);
            extras.put("orderId", orderId);
            extras.put("paymentMethod", paymentMethod);

            StringBuilder productIds = new StringBuilder();
            StringBuilder quantities = new StringBuilder();
            StringBuilder prices = new StringBuilder();
            StringBuilder currencies = new StringBuilder();
            StringBuilder variantIds = new StringBuilder();

            if(items != null && items.length > 0) {
                for (CardItem item:items) {
                    productIds.append(item.getProductId()).append("|");
                    quantities.append(item.getQuantity()).append("|");
                    prices.append(item.getPrice()).append("|");
                    currencies.append(item.getCurrency()).append("|");
                    variantIds.append(item.getVariantId()).append("|");
                }
            }

            extras.put("productIds", productIds.toString());
            extras.put("quantities", quantities.toString());
            extras.put("prices", prices.toString());
            extras.put("currencies", currencies.toString());
            extras.put("variantIds", variantIds.toString());

            sendEvent("PV", extras);
        } catch (Exception ignored) { }
    }

    public void sendPageView(Map<String, Object> extras) {
        try {
            sendEvent("pageView", extras);
        } catch (Exception ignored) { }
    }

    private void sendEvent(String eventName, Map<String, Object> data) {
        logger.Verbose("sendEvent method is called");
        try {
            Subscription subscription = DengageManager.getInstance(_context).getSubscription();
            DenEvent event = new DenEvent();
            event.setIntegrationKey(subscription.getIntegrationKey());
            event.setSessionId(Session.getSession().getSessionId());
            event.setDeviceId(subscription.getDeviceId());
            event.setEventName(eventName);
            event.setTestGroup(subscription.getTestGroup());
            event.setContactKey(subscription.getContactKey());
            event.setParams(data);

            logger.Debug("sendEvent: "+ eventName);

            RequestAsync req = new RequestAsync(event);
            req.execute();

        } catch (Exception e) {
            logger.Error("sendCustomDenEvent: "+ e.getMessage());
        }
    }
}
