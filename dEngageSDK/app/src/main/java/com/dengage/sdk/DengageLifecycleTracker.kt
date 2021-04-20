package com.dengage.sdk

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Created by Batuhan Coskun on 19 February 2021
 */
class DengageLifecycleTracker : Application.ActivityLifecycleCallbacks {

    private var startedActivityCount = 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (startedActivityCount == 0) {
            // app went to foreground
            DengageManager.getInstance(activity)?.getInAppMessages()
        }
        startedActivityCount++
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivityCount--
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}