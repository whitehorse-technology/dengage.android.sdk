package com.dengage.sdk;

import android.os.AsyncTask;

import com.dengage.sdk.models.DenEvent;
import com.dengage.sdk.models.Event;
import com.dengage.sdk.models.ModelBase;
import com.dengage.sdk.models.Open;
import com.dengage.sdk.models.Subscription;
import com.dengage.sdk.models.TransactionalOpen;

class RequestAsync extends AsyncTask<Void, Void, Void> {

    private ModelBase model;

    RequestAsync(ModelBase model) {
        this.model = model;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Request req = new Request();

        if(this.model instanceof Subscription) {
            req.sendSubscription((Subscription) this.model);
        }

        else if(this.model instanceof Open) {
            req.sendOpen((Open) this.model);
        }

        else if(this.model instanceof TransactionalOpen) {
            req.sendTransactionalOpen((TransactionalOpen) this.model);
        }

        else if(this.model instanceof Event) {
            req.sendEvent((Event) this.model);
        }

        else  if(this.model instanceof DenEvent) {
            req.sendEcEvent((DenEvent) this.model);
        }

        return null;
    }
}