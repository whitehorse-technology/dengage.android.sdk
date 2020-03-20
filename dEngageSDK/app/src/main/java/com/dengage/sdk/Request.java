package com.dengage.sdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.dengage.sdk.models.DenEvent;
import com.dengage.sdk.models.ModelBase;
import com.google.gson.Gson;
import androidx.annotation.NonNull;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

class Request  {

    private final int connectionTimeout = 15000;
    private final int readTimeout = 10000;
    private Logger logger = Logger.getInstance();

    boolean send(String url, String userAgent, ModelBase model, @NonNull Type modelType) {
        logger.Verbose("sendReuqest to: "+ url);

        HttpURLConnection conn = null;
        OutputStream os = null;
        int responseCode = 0;
        String responseMessage = "";
        try {

            URL uri = new URL(url);
            Gson gson = new Gson();
            String message = gson.toJson(model, modelType);
            logger.Verbose("sendReuqest body: " + message);
            conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(readTimeout);
            conn.setConnectTimeout(connectionTimeout);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(message.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setRequestProperty("User-Agent", userAgent);
            conn.connect();
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(message.getBytes());
            os.flush();
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
        } catch (Exception e) {
            logger.Error( "sendRequest: "+ e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                logger.Error( "sendRequest finally: "+ e.getMessage());
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        logger.Verbose("Response Message: "+ responseMessage);
        logger.Verbose("Response Message: "+ responseCode);

        try {

            if(responseCode <= 199 || responseCode >= 300)
                throw new Exception("The remote server returned an error with the status code: "+ responseCode);

        } catch(Exception e) {
            logger.Error(e.getMessage());
        }

        return responseCode > 199 && responseCode < 300;
    }

    void sendEvent(DenEvent event) {
        try {
            String url = Constants.ecApiEndpoint + "/SDK-KEY";
            String json = event.toJson();
            String data = URLEncoder.encode(json, "utf-8");
            String postData = Base64.encodeToString(data.getBytes(), Base64.DEFAULT);

            logger.Verbose("json: " + json);
            logger.Verbose("data: " + data);
            logger.Verbose("postData: " + postData);

            sendRequest(url, postData, "text/plain");

        } catch (Exception e) {
            logger.Error("sendEvent: "+ e.getMessage());
        }
    }

    private void sendRequest(String url, String data, String contentType) {
        try {
            URL uri = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(readTimeout);
            conn.setConnectTimeout(connectionTimeout);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", contentType);
            conn.setFixedLengthStreamingMode(data.getBytes().length);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(data.getBytes());
            os.flush();
            int responseCode = conn.getResponseCode();
            os.close();
            conn.disconnect();
            if(responseCode <= 199 || responseCode >= 300)
                throw new Exception("The remote server returned an error with the status code: "+ responseCode);
        } catch (Exception e) {
            logger.Error( "sendEvent: "+ e.getMessage());
        }
    }
}