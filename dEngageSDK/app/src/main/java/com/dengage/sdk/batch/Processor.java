package com.dengage.sdk.batch;

import com.dengage.sdk.batch.database.DengageDatabase;
import com.dengage.sdk.batch.database.ModelEntity;
import com.dengage.sdk.batch.database.dao.ModelDao;
import com.dengage.sdk.models.Event;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Processor {

    private final String url;
    private final ModelDao dao;
    private final String userAgent;

    private long transactionId = System.currentTimeMillis();
    private int count = 0;
    private int MAX_COUNT = 40;
    private Logger logger = Logger.getInstance();

    public Processor(String url, DengageDatabase database, String userAgent) {
        this.url = url;
        dao = database.modelDao();
        this.userAgent = userAgent;
    }

    private class EventBundle {
        private List<ModelEntity> modelEntities;

        public EventBundle(List<ModelEntity> modelEntities) {
            this.modelEntities = modelEntities;
        }
    }

    public void flushAll() {
        send();
    }

    private boolean sendBundle(EventBundle bundle) {
        List<String> jsons = new ArrayList<>();
        for (ModelEntity modelEntity : bundle.modelEntities) {
            jsons.add(modelEntity.toJson());
        }

        Gson gson = new Gson();
        String s = gson.toJson(jsons);
        return sendRequest(url, s, "application/json");
    }

    private boolean sendRequest(String url, String data, String contentType) {
        try {
            URL uri = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            int readTimeout = 10000;
            conn.setReadTimeout(readTimeout);
            int connectionTimeout = 15000;
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
            if (responseCode <= 199 || responseCode >= 300) {
                return false;
            }

            return true;
                throw new Exception("The remote server returned an error with the status code: " + responseCode);

        } catch (Exception e) {
            return false;
            logger.Error( "sendRequest: "+ e.getMessage());
        }
    }

    private EventBundle pack(List<ModelEntity> invalids) {
        return new EventBundle(invalids);
    }

    private boolean send() {
        List<ModelEntity> currentEntities = dao.getAll();
        EventBundle pack = pack(currentEntities);
        if (sendBundle(pack)) {
            for (ModelEntity currentEntity : currentEntities) {
                dao.delete(currentEntity);
            }

            transactionId = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    private List<ModelEntity> findModels(long transactionId) {
        return null;
    }

    // ExecutorThread
    public void save(Event model) {
        ModelEntity modelEntity = new ModelEntity();
        modelEntity.inegrationKey = model.integrationKey;
        modelEntity.transactionId = transactionId;
        modelEntity.json = model.toJson();
        dao.insertAll(modelEntity);

        count++;

        checkCountThenMaybeFlush();
    }

    private void checkCountThenMaybeFlush() {
        if (count == MAX_COUNT) {
            count = 0;
            send();
        }
    }
}
