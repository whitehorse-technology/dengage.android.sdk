package com.dengage.sdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

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

class Request  {

    private Logger logger = Logger.getInstance();

    boolean send(String url, String userAgent, ModelBase model, @NonNull Type modelType) {
        logger.Verbose("sendReuqest to: "+ url);

        final int connectionTimeout = 15000;
        final int readTimeout = 10000;

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
}