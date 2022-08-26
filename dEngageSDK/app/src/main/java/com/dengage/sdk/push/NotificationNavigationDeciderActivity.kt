package com.dengage.sdk.push

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dengage.sdk.DengageManager
import com.dengage.sdk.NotificationReceiver
import com.dengage.sdk.NotificationReceiver.getActivity
import com.dengage.sdk.R
import com.dengage.sdk.Utils
import com.dengage.sdk.models.Message
import com.huawei.hms.utils.Util

class NotificationNavigationDeciderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_navigation_decider)

        if (intent != null) {
            val extras = intent.extras
            var uri: String? = null
            if (extras != null) {
                uri = extras.getString("targetUrl")

                if (uri != null && !TextUtils.isEmpty(uri)) {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                } else {
                    val packageName: String = Utils.getPackageName(this)

                    intent = Intent(this@NotificationNavigationDeciderActivity, getActivity(this))
                    intent.putExtras(extras)
                    intent.setPackage(packageName)
                }
                var message: Message? = Message(extras)
                val rawJson = extras.getString("RAW_DATA")
                if (!TextUtils.isEmpty(rawJson)) message = Message.fromJson(rawJson)
                callOpenEvent(message)
                clearNotifications(message)
            } else {
                val packageName: String = Utils.getPackageName(this)

                intent = Intent(this@NotificationNavigationDeciderActivity, getActivity(this))
                intent.setPackage(packageName)

            }

            startActivity(intent)
            finishAffinity()
        }
    }


    private fun clearNotifications(message: Message?) {
        NotificationReceiver.clearNotification(this, message)
    }

    private fun callOpenEvent(message: Message?) {
        DengageManager.getInstance(this).sendOpenEvent("", "", message)

    }
}