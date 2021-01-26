package com.dengage.sdk;

import com.dengage.sdk.models.Event;
import com.dengage.sdk.models.Open;
import com.dengage.sdk.models.Subscription;
import com.dengage.sdk.models.TransactionalOpen;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {

    private Logger logger = Logger.getInstance();

    void sendSubscription(String url, Subscription model) {
        try {
            String json = model.toJson();
            String userAgent = model.getUserAgent();
            logger.Verbose("sendSubscription: " + url + " with the json: " + json);
            sendRequest(url, "POST", userAgent, json);
        } catch (Exception e) {
            logger.Error("sendSubscription: " + e.getMessage());
        }
    }

    void sendOpen(String url, Open model) {
        try {
            String json = model.toJson();
            String userAgent = model.getUserAgent();
            logger.Verbose("sendOpen: " + url + " with the json: " + json);
            sendRequest(url, "POST", userAgent, json);
        } catch (Exception e) {
            logger.Error("sendSubscription: " + e.getMessage());
        }
    }

    void sendTransactionalOpen(String url, TransactionalOpen model) {
        try {
            String json = model.toJson();
            String userAgent = model.getUserAgent();
            logger.Verbose("sendTransactionalOpen: " + url + " with the json: " + json);
            sendRequest(url, "POST", userAgent, json);
        } catch (Exception e) {
            logger.Error("sendSubscription: " + e.getMessage());
        }
    }

    void sendEvent(String url, Event model) {
        try {
            String json = model.toJson();
            String userAgent = model.getUserAgent();
            logger.Verbose("sendEvent: " + url + " with the json: " + json);
            sendRequest(url, "POST", userAgent, json);
        } catch (Exception e) {
            logger.Error("sendEvent: " + e.getMessage());
        }
    }

    public String getSdkParameters(String url, String userAgent) {
        try {
            logger.Verbose("getSdkParameters: " + url);
            return sendRequest(url, "GET", userAgent, null);
        } catch (Exception e) {
            logger.Error("getSdkParameters: " + e.getMessage());
            return null;
        }
    }

    private String sendRequest(String url, String methodType, String userAgent, String data) {
        try {
            URL uri = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            int readTimeout = 10000;
            conn.setReadTimeout(readTimeout);
            int connectionTimeout = 15000;
            conn.setConnectTimeout(connectionTimeout);
            conn.setRequestMethod(methodType);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("User-Agent", userAgent);
            conn.connect();
            if (data != null) {
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(data.getBytes());
                os.flush();
                os.close();
            }
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();

            String response = null;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = readStream(conn.getInputStream());
            }

            conn.disconnect();
            logger.Verbose("The remote server response: " + response);
            logger.Verbose("The remote server responseCode: " + responseCode);
            logger.Verbose(responseMessage);
            if (responseCode <= 199 || responseCode >= 300) {
                throw new Exception("The remote server returned an error with the status code: " + responseCode);
            }
            return response;
        } catch (Exception e) {
            logger.Error("sendRequest: " + e.getMessage());
            return null;
        }
    }

    // Converting InputStream to String

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}