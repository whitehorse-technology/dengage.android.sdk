package com.dengage.sdk;

import android.util.Base64;

import com.dengage.sdk.models.EcEvent;
import com.dengage.sdk.models.Event;
import com.dengage.sdk.models.Open;
import com.dengage.sdk.models.Subscription;
import com.dengage.sdk.models.TransactionalOpen;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class Request  {

    private final int connectionTimeout = 15000;
    private final int readTimeout = 10000;
    private Logger logger = Logger.getInstance();

    void sendSubscription(Subscription model) {
        try {
            String url = model.isValidSubscriptionUri() ? model.getSubscriptionUri() : Constants.SUBS_API_ENDPOINT;
            String json = model.toJson();
            String userAgent = model.userAgent;
            logger.Verbose("sendSubscription: " + url + " with the json: "+ json);
            sendRequest(url, userAgent, json, "application/json");
        } catch (Exception e) {
            logger.Error("sendSubscription: "+ e.getMessage());
        }
    }

    void sendOpen(Open model) {
        try {
            String url = Constants.OPEN_API_ENDPOINT;
            String json = model.toJson();
            String userAgent = model.userAgent;
            logger.Verbose("sendOpen: " + url + " with the json: "+ json);
            sendRequest(url, userAgent, json, "application/json");
        } catch (Exception e) {
            logger.Error("sendSubscription: "+ e.getMessage());
        }
    }

    void sendTransactionalOpen(TransactionalOpen model) {
        try {
            String url = Constants.TRANS_OPEN_API_ENDPOINT;
            String json = model.toJson();
            String userAgent = model.userAgent;
            logger.Verbose("sendTransactionalOpen: " + url + " with the json: "+ json);
            sendRequest(url, userAgent, json, "application/json");
        } catch (Exception e) {
            logger.Error("sendSubscription: "+ e.getMessage());
        }
    }

    void sendEvent(Event model) {
        try {
            String url = Constants.EVENT_API_ENDPOINT;
            String json = model.toJson();
            String userAgent = model.userAgent;
            logger.Verbose("sendEvent: " + url + " with the json: "+ json);
            sendRequest(url, userAgent, json, "application/json");
        } catch (Exception e) {
            logger.Error("sendEvent: "+ e.getMessage());
        }
    }

    void sendEcEvent(EcEvent model) {
        try {
            String url = Constants.EC_API_ENDPOINT + "/"+ model.integrationKey;
            String json = model.toJson();
            String data = URLEncoder.encode(json, "utf-8");
            String postData = Base64.encodeToString(data.getBytes(), Base64.DEFAULT).replaceAll("(\\s|\\r\\n|\\r|\\n)", "");

            logger.Verbose("sendEcEvent: " + url + " with the json: "+ json);
            logger.Verbose("sendEcEvent: " + data);
            logger.Verbose("sendEcEvent: " + postData);

            sendRequest(url, "", postData, "text/plain");

        } catch (Exception e) {
            logger.Error("sendDenEvent: "+ e.getMessage());
        }
    }

    private void sendRequest(String url, String userAgent, String data, String contentType) {
        try {
            URL uri = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(readTimeout);
            conn.setConnectTimeout(connectionTimeout);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("User-Agent", userAgent);
            conn.setFixedLengthStreamingMode(data.getBytes().length);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(data.getBytes());
            os.flush();
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();
            os.close();
            conn.disconnect();
            logger.Verbose("The remote server response: "+ responseCode);
            logger.Verbose(responseMessage);
            if(responseCode <= 199 || responseCode >= 300)
                throw new Exception("The remote server returned an error with the status code: "+ responseCode);
        } catch (Exception e) {
            logger.Error( "sendRequest: "+ e.getMessage());
        }
    }
}