package com.dengage.sdk;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dengage.sdk.models.CardItem;
import com.dengage.sdk.models.DenEvent;
import com.dengage.sdk.models.Event;
import com.dengage.sdk.models.Session;
import com.dengage.sdk.models.Subscription;

import java.util.HashMap;
import java.util.Map;



public class DengageEvent {

    private static Logger logger = Logger.getInstance();

    @SuppressLint("StaticFieldLeak")
    private static Context _context;

    public DengageEvent(Context context) {
        _context = context;
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
            Subscription subscription = DengageManager.getInstance(_context).getSubscription();
            Event event = new Event(subscription.getIntegrationKey(), tableName, key, data);
            event.setUserAgent(Utils.getUserAgent(_context));
            logger.Debug("sendCustomEvent: " + event.toJson());
            RequestAsync req = new RequestAsync(event);
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
    public void sendDeviceEvent(String tableName, Map<String, Object> data) {
        logger.Verbose("sendDeviceEvent method is called");
        try {
            Subscription subscription = DengageManager.getInstance(_context).getSubscription();
            Event event = new Event(subscription.getIntegrationKey(), tableName, subscription.getDeviceId(), data);
            event.setUserAgent(Utils.getUserAgent(_context));
            logger.Debug("sendDeviceEvent: " + event.toJson());
            RequestAsync req = new RequestAsync(event);
            req.execute();
        } catch (Exception e) {
            logger.Error("sendDeviceEvent: "+ e.getMessage());
        }
    }

    public void startSession(String actionUrl) {
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
        extras.put("advertisingId", subscription.getAdvertisingId());
        extras.put("carrierId", subscription.getCarrierId());
        extras.put("token", subscription.getToken());
        extras.put("appVersion", subscription.getAppVersion());
        extras.put("permission", subscription.getPermission());
        extras.put("os", Utils.getOsVersion());
        extras.put("model", Utils.getModel());
        extras.put("manufacturer", Utils.getManufacturer());
        extras.put("brand", Utils.getBrand());
        extras.put("deviceUniqueId", Utils.getDeviceUniqueId());
        sendCustomDenEvent("startSession", actionUrl, extras);
    }

    public void tokenRefresh(String token) {
        HashMap<String, Object> extras = new HashMap<>();
        extras.put("token", token);

        sendCustomDenEvent("tokenRefresh", null, extras);
    }

    public void productDetail(String productId, Double price, Double discountedPrice, String currency, String supplierId) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("entityType", "product");
        extras.put("entityId", productId);
        extras.put("price", price);
        extras.put("discountedPrice", discountedPrice);
        extras.put("currency", currency);
        extras.put("supplierId", supplierId);
        extras.put("pageType", "productDetail");

        sendCustomDenEvent("PV", null, extras);
    }

    public void promotionPage(String promotionId) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("entityType", "promotion");
        extras.put("entityId", promotionId);
        extras.put("pageType", "promotionPage");

        sendCustomDenEvent("PV", null, extras);
    }

    public void CategoryPage(String categoryId, String parentCategoryId) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("entityType", "category");
        extras.put("entityId", categoryId);
        extras.put("parentCategory", parentCategoryId);
        extras.put("pageType", "categoryPage");

        sendCustomDenEvent("PV", null, extras);
    }

    public void HomePage() {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("pageType", "homePage");

        sendCustomDenEvent("PV", null, extras);
    }

    public void SearchPage(String keyword, long resultCount) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("pageType", "searchPage");
        extras.put("keyword", keyword);
        extras.put("resultCount", resultCount);

        sendCustomDenEvent("PV", null, extras);
    }

    public void Refinement(Map<String, String[]> filters, long resultCount) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("pageType", "searchPage");
        extras.put("filters", filters);
        extras.put("resultCount", resultCount);
        extras.put("entityType", "products");

        sendCustomDenEvent("Action", null, extras);
    }

    public void LoginPage() {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("pageType", "loginPage");

        sendCustomDenEvent("PV", null, extras);
    }

    public void LoginAction(String memberId, String status, String origin) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("eventType", "loginAction");
        extras.put("origin", origin);
        extras.put("success", status);
        extras.put("memberId", memberId);

        sendCustomDenEvent("Action", null, extras);
    }

    public void RegisterPage() {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("pageType", "registerPage");

        sendCustomDenEvent("PV", null, extras);
    }

    public void RegisterAction(String memberId, String status, String origin) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("eventType", "registerAction");
        extras.put("origin", origin);
        extras.put("success", status);
        extras.put("memberId", memberId);

        sendCustomDenEvent("Action", null, extras);
    }

    public void AddToBasket(CardItem item, String origin, String basketId) {

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

        sendCustomDenEvent("Action", null, extras);
    }

    public void RemoveFromBasket(String productId, String variantId, int quantity, String basketId) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("eventType", "removeFromBasket");
        extras.put("productId", productId);
        extras.put("variantId", variantId);
        extras.put("quantity", quantity);
        extras.put("basketId", basketId);

        sendCustomDenEvent("Action", null, extras);
    }

    public void BasketPage(CardItem[] items, Double totalPrice, String basketId) {

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

        sendCustomDenEvent("PV", null, extras);
    }

    public void OrderSummary(CardItem[] items, String basketId, Double totalPrice, String orderId, String paymentMethod) {

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("pageType", "orderSummary");
        extras.put("basketId", basketId);
        extras.put("totalPrice", totalPrice);
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

        sendCustomDenEvent("PV", null, extras);
    }

    public void sendPageView(Map<String, Object> data) {
        try {
            sendCustomDenEvent("pageView", "", data);
        } catch (Exception ignored) { }
    }

    public void sendCustomDenEvent(String eventName, String actionUrl, Map<String, Object> data) {
        logger.Verbose("sendCustomDenEvent method is called");
        try {

            Subscription subscription = DengageManager.getInstance(_context).getSubscription();
            DenEvent event = new DenEvent();
            event.setIntegrationKey(subscription.getIntegrationKey());
            event.setEventName(eventName);
            event.setSessionId(Session.getSession().getSessionId());
            event.setPersistentId(Utils.getDeviceId(_context));
            event.setTestGroup(subscription.getTestGroup());
            event.setMemberId(subscription.getContactKey());
            event.setParams(data);

            logger.Debug(""+ eventName +" : " + event.toJson());

            RequestAsync req = new RequestAsync(event);
            req.execute();

        } catch (Exception e) {
            logger.Error("sendCustomDenEvent: "+ e.getMessage());
        }
    }

    public void sendSubscription() {
        try {
            sendCustomDenEvent("subscription", "", null);
        } catch (Exception e) { }
    }
}
