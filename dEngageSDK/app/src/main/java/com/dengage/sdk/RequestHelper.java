package com.dengage.sdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.dengage.sdk.models.ModelBase;
import com.google.gson.Gson;
import androidx.annotation.NonNull;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

public final class RequestHelper  {

    private final int connectionTimeout = 15000;
    private final int readTimeout = 10000;
    private Gson gson = new Gson();

    public static RequestHelper INSTANCE = new RequestHelper();

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

    public void sendRequestAsync(String url, String userAgent, ModelBase model, @NonNull Type type) {
        new JsonAsyncTask(url, userAgent, model, type).execute();
    }

    public boolean sendRequest(String url, String userAgent, ModelBase model, @NonNull Type modelType) {
        Logger.INSTANCE.Verbose("sendReuqest to: "+ url);

        HttpURLConnection conn = null;
        OutputStream os = null;
        int responseCode = 0;
        String responseMessage = "";
        try {
            URL uri = new URL(url);
            Logger.INSTANCE.Verbose("sendReuqest: Request body parsing...");
            String message = gson.toJson(model, modelType);
            Logger.INSTANCE.Verbose("sendReuqest:  Request for " + url + " with : " + message);
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
            Logger.INSTANCE.Error( "sendRequest: "+ e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                Logger.INSTANCE.Error( "sendRequest finally: "+ e.getMessage());
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        Logger.INSTANCE.Verbose("Response Message: "+ responseMessage);
        Logger.INSTANCE.Verbose("Response Message: "+ responseCode);

        try {

            if(responseCode <= 199 || responseCode >= 300)
                throw new Exception("The remote server returned an error with the status code: "+ responseCode);

        } catch(Exception e) {
            Logger.INSTANCE.Error(e.getMessage());
        }

        return responseCode > 199 && responseCode < 300;
    }

    private class JsonAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<String> urlReference;
        private WeakReference<ModelBase> modelReference;
        private WeakReference<Type> modelTypeReference;
        private WeakReference<String> userAgentReference;

        JsonAsyncTask(String url, String userAgent, ModelBase model, Type modelType){
            urlReference = new WeakReference<>(url);
            modelReference = new WeakReference<>(model);
            modelTypeReference = new WeakReference<>(modelType);
            userAgentReference = new WeakReference<>(userAgent);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sendRequest(urlReference.get(), userAgentReference.get(), modelReference.get(), modelTypeReference.get());
            return null;
        }
    }

}
