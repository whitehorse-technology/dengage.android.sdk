package com.dengage.sdk;

import android.os.AsyncTask;
import android.os.Build;

import com.dengage.sdk.models.Event;
import com.dengage.sdk.models.ModelBase;
import com.dengage.sdk.models.Open;
import com.dengage.sdk.models.Subscription;
import com.dengage.sdk.models.TransactionalOpen;

class RequestAsync extends AsyncTask<Void, Void, Void> {

    private ModelBase model;
    private String url;

    RequestAsync(ModelBase model) {
        this.model = model;
    }

    RequestAsync(String url, ModelBase model) {
        this.model = model;
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Request req = new Request();

        if (this.model instanceof Subscription) {
            req.sendSubscription(url, (Subscription) this.model);
        } else if (this.model instanceof Open) {
            req.sendOpen(url, (Open) this.model);
        } else if (this.model instanceof TransactionalOpen) {
            req.sendTransactionalOpen(url, (TransactionalOpen) this.model);
        } else if (this.model instanceof Event) {
            req.sendEvent(url, (Event) this.model);
        }

        return null;
    }

    public void executeTask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            this.execute();
    }
}
