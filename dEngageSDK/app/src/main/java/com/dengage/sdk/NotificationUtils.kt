package com.dengage.sdk

import android.R
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat


class NotificationUtils {

    fun areNotificationsEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!manager.areNotificationsEnabled()) {
                return false
            }
            val channels = manager.notificationChannels
            for (channel in channels) {
                if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                    return false
                }
            }
            true
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun showAlert(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Enable Push Notification")
            .setMessage("You need to enable push notifications") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    launchSettingsActivity(context)
                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setCancelable(false)
            .show()
    }

    fun launchSettingsActivity(context: Context) {


        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
        } else {
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:$context.packageName")
        }

        context.startActivity(intent)
    }

    fun registerBroadcast(context: Context) {
        try {
            val filter = IntentFilter(Constants.PUSH_RECEIVE_EVENT)
            filter.addAction(Constants.PUSH_OPEN_EVENT)
            filter.addAction(Constants.PUSH_DELETE_EVENT)
            filter.addAction(Constants.PUSH_ACTION_CLICK_EVENT)
            filter.addAction(Constants.PUSH_ITEM_CLICK_EVENT)
            filter.addAction("com.dengage.push.intent.CAROUSEL_ITEM_CLICK")
            context.applicationContext.registerReceiver(
                NotificationReceiver(),
                filter
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unregisterBroadcast(context: Context) {
        try {
            context.unregisterReceiver(NotificationReceiver())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendBroadCast(intent: Intent, context: Context) {
        try {
            val broadCastIntent = Intent(intent.action)
            broadCastIntent.putExtras(intent.extras!!)
            context.sendBroadcast(broadCastIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
