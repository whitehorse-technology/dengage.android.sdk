package com.dengage.sdk.notification.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.dengage.sdk.notification.Constants;
import com.dengage.sdk.notification.logging.Logger;
import com.dengage.sdk.notification.models.ModelBase;
import com.google.gson.Gson;
import androidx.annotation.NonNull;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import com.dengage.sdk.BuildConfig;

public final class RequestHelper {

    private static final int connectionTimeout = 15000;
    private static final int readTimeout = 10000;
    private static Gson gson = new Gson();
    private static RequestHelper instance;

    public static RequestHelper getInstance() {
        return instance;
    }

    static {
        instance = new RequestHelper();
    }

    private RequestHelper() {
    }

    public Bitmap getBitmap(String urlString) {
        try {
            return BitmapFactory.decodeStream(new URL(urlString).openConnection().getInputStream());
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void sendRequestAsync(String url, ModelBase model, Type type) {
        new JsonAsyncTask(url, model, type).execute();
    }

    public boolean sendRequest(String url, ModelBase model, @NonNull Type modelType) {
        HttpURLConnection conn = null;
        OutputStream os = null;
        int responseCode = 0;
        String responseMessage = "";
        try {
            URL uri = new URL(url);
            String message = gson.toJson(model, modelType);
            Logger.Debug("Request to : " + model.getClass().getName() + " with : " + message);
            conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(readTimeout);
            conn.setConnectTimeout(connectionTimeout);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(message.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("Accept","application/json");
            conn.connect();
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(message.getBytes());
            os.flush();
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
        } catch (Exception e) {
            Logger.Error(e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                Logger.Error(e.getMessage());
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            if(responseCode <= 199 || responseCode >= 300)
                throw new Exception("The remote server returned an error with the status code: "+ responseCode);
        } catch(Exception e) {
            Logger.Error(e.getMessage());
        }

        Logger.Debug("Request: " + model.getClass().getName());
        Logger.Debug("Server Response Code : "+ responseCode);
        Logger.Debug("Response Response Text : " + responseMessage);

        return responseCode > 199 && responseCode < 300;
    }

    private class JsonAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<String> urlReference;
        private WeakReference<ModelBase> modelReference;
        private WeakReference<Type> modelTypeReference;

        JsonAsyncTask(String url, ModelBase model, Type modelType){
            urlReference = new WeakReference<>(url);
            modelReference = new WeakReference<>(model);
            modelTypeReference = new WeakReference<>(modelType);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sendRequest(urlReference.get(), modelReference.get(), modelTypeReference.get());
            return null;
        }
    }
}
