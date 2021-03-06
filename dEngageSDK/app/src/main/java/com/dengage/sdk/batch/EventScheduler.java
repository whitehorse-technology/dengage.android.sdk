package com.dengage.sdk.batch;


import android.util.Log;

import com.dengage.sdk.models.Event;
import com.dengage.sdk.models.ModelBase;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Singleton
public class EventScheduler {
    private final ExecutorService executorService;
    private final Processor processor;
    private final Timer timer;

    public EventScheduler(Processor processor, int period) {
        executorService = Executors.newSingleThreadExecutor();
        this.processor = processor;
        timer = new Timer("Timer " + period + " seconds");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                flush();
            }
        }, period * 1000, period * 1000);
    }

    public void teardown() {
        timer.cancel();
    }

    public void flush() {
        executorService.submit(() -> {
            processor.flushAll();
        });
    }

    class SingleEvent {

        private final String url;
        private final ModelBase model;

        SingleEvent(String url, ModelBase model) {
            this.url = url;
            this.model = model;
        }
    }

    public void schedule(final Event model) {
        executorService.submit(() -> {
            try {
                processor.save(model);
            } catch (Exception e) {
                Log.e("Scheduler", "Error while caching event", e);
            }
        });
    }
}
