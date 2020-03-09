package com.dengage.sdk;

import android.os.AsyncTask;

import com.dengage.sdk.models.ModelBase;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

class RequestAsync extends AsyncTask<Void, Void, Void> {

    private String _url;
    private String _userAgent;
    private ModelBase _model;
    private Type _modelType;

    RequestAsync(String url, String userAgent, ModelBase model, Type modelType){
        _url = url;
        _userAgent = userAgent;
        _model = model;
        _modelType = modelType;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Request req = new Request();
        req.send(_url, _userAgent, _model, _modelType);
        return null;
    }
}
