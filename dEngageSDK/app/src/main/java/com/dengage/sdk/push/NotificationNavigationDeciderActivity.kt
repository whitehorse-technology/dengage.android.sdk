package com.dengage.sdk.push

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.dengage.sdk.NotificationReceiver.getActivity
import com.dengage.sdk.NotificationUtils
import com.dengage.sdk.Utils
import com.dengage.sdk.cache.GsonHolder
import com.dengage.sdk.models.Message

class NotificationNavigationDeciderActivity : Activity() {

    private val notificationUtils = NotificationUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onPause() {
        super.onPause()

        killActivity()


    }

    override fun onResume() {
        super.onResume()

        val sendingIntentObject: Intent

        if (intent != null) {

            val extras = intent.extras

            val uri: String?

            if (extras != null) {

                uri = extras.getString("targetUrl")


                if (uri != null && !TextUtils.isEmpty(uri)) {

                    sendingIntentObject = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

                } else {

                    val packageName: String = Utils.getPackageName(this)

                    sendingIntentObject =
                        Intent(this@NotificationNavigationDeciderActivity, getActivity(this))

                    sendingIntentObject.putExtras(extras)

                    sendingIntentObject.setPackage(packageName)

                }


                var message: Message? = Message(extras)

                val rawJson = extras.getString("RAW_DATA")


                if (!TextUtils.isEmpty(rawJson)) {

                    message = GsonHolder.gson.fromJson(rawJson, Message::class.java)

                }
                notificationUtils.sendBroadCast(intent,this)

                startActivity(sendingIntentObject)


            } else {

                val packageName: String = packageName

                sendingIntentObject =
                    Intent(this@NotificationNavigationDeciderActivity, getActivity(this))

                sendingIntentObject.setPackage(packageName)
                startActivity(sendingIntentObject)

            }
            killActivity()
        }
    }

    private fun killActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            notificationUtils.unregisterBroadcast(this)
            onDestroy()
        }, 1200)
    }
}