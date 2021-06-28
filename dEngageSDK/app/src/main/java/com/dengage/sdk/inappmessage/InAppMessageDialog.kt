package com.dengage.sdk.inappmessage

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.dengage.sdk.NotificationReceiver
import com.dengage.sdk.R
import com.dengage.sdk.inappmessage.model.ContentParams
import com.dengage.sdk.inappmessage.model.ContentPosition
import com.dengage.sdk.inappmessage.model.InAppMessage
import com.dengage.sdk.inappmessage.utils.InAppMessageUtils
import com.dengage.sdk.models.TagItem
import kotlin.math.roundToInt

/**
 * Created by Batuhan Coskun on 26 February 2021
 */
class InAppMessageDialog : DialogFragment(), View.OnClickListener {

    private lateinit var inAppMessage: InAppMessage
    private var inAppMessageCallback: InAppMessageCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_FullViewDialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_in_app_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inAppMessage = requireArguments().getSerializable(EXTRA_IN_APP_MESSAGE) as InAppMessage
        val contentParams = inAppMessage.data.content.params

        dialog?.setCanceledOnTouchOutside(
            inAppMessage.data.content.params.dismissOnTouchOutside ?: true
        )
        dialog?.setOnCancelListener {
            inAppMessageDismissed()
        }

        setContentPosition(view, contentParams)
        setHtmlContent(view, contentParams)

        view.findViewById<View>(R.id.vInAppMessageContainer).setOnClickListener(this)
        view.findViewById<View>(R.id.cardInAppMessage).setOnClickListener(this)
    }

    private fun setContentPosition(
        view: View, contentParams: ContentParams
    ) {
        val cardInAppMessage = view.findViewById<CardView>(R.id.cardInAppMessage)
        val params = RelativeLayout.LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        )
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        params.setMargins(
            InAppMessageUtils.getPixelsByPercentage(screenWidth, contentParams.marginLeft),
            InAppMessageUtils.getPixelsByPercentage(screenHeight, contentParams.marginTop),
            InAppMessageUtils.getPixelsByPercentage(screenWidth, contentParams.marginRight),
            InAppMessageUtils.getPixelsByPercentage(screenHeight, contentParams.marginBottom)
        )
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        when (contentParams.position) {
            ContentPosition.BOTTOM.position -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            ContentPosition.MIDDLE.position -> {
                params.addRule(RelativeLayout.CENTER_VERTICAL)
            }
            ContentPosition.TOP.position -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            }
        }
        cardInAppMessage.layoutParams = params
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setHtmlContent(view: View, contentParams: ContentParams) {
        val vHtmlContent = view.findViewById<View>(R.id.vHtmlContent)
        val webView = view.findViewById<WebView>(R.id.webView)
        val vHtmlWidthContainer = view.findViewById<RelativeLayout>(R.id.vHtmlWidthContainer)
        val cardInAppMessage = view.findViewById<CardView>(R.id.cardInAppMessage)

        // set height for content type full
        if (contentParams.position == ContentPosition.FULL.position) {
            val params = RelativeLayout.LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT
            )
            webView.layoutParams = params
        }

        // set radius of card view
        cardInAppMessage.radius = InAppMessageUtils.pxToDp(contentParams.radius, requireContext())

        // set max width for container
        contentParams.maxWidth?.let {
            val params = vHtmlWidthContainer.layoutParams as ConstraintLayout.LayoutParams
            params.matchConstraintMaxWidth = InAppMessageUtils.pxToDp(it, requireContext()).roundToInt()
            vHtmlWidthContainer.layoutParams = params
        }

        vHtmlContent.visibility = View.VISIBLE

        webView.apply {
            loadDataWithBaseURL(
                null,
                contentParams.html, "text/html", "UTF-8", null
            )
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.displayZoomControls = false
            settings.builtInZoomControls = true
            settings.setSupportZoom(true)
            setBackgroundColor(Color.TRANSPARENT)
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            addJavascriptInterface(JavaScriptInterface(), "Dn")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.vInAppMessageContainer -> {
                if (inAppMessage.data.content.params.dismissOnTouchOutside != false) {
                    dismiss()
                    inAppMessageDismissed()
                }
            }
            R.id.cardInAppMessage -> {
                // ignore
            }
        }
    }

    private fun inAppMessageDismissed() {
        inAppMessageCallback?.inAppMessageDismissed(inAppMessage)
    }

    override fun onDestroy() {
        inAppMessageCallback = null
        super.onDestroy()
    }

    fun setInAppMessageCallback(inAppMessageCallback: InAppMessageCallback) {
        this.inAppMessageCallback = inAppMessageCallback
    }

    interface InAppMessageCallback {
        fun inAppMessageClicked(inAppMessage: InAppMessage, buttonId: String?)
        fun inAppMessageDismissed(inAppMessage: InAppMessage)
        fun sendTags(tags: List<TagItem>?)
    }

    companion object {
        const val EXTRA_IN_APP_MESSAGE = "EXTRA_IN_APP_MESSAGE"
        fun newInstance(inAppMessage: InAppMessage): InAppMessageDialog {
            val inAppMessageDialog = InAppMessageDialog()
            val arguments = Bundle()
            arguments.putSerializable(EXTRA_IN_APP_MESSAGE, inAppMessage)
            inAppMessageDialog.arguments = arguments
            return inAppMessageDialog
        }
    }

    private inner class JavaScriptInterface {
        @JavascriptInterface
        fun dismiss() {
            this@InAppMessageDialog.dismiss()
            inAppMessageDismissed()
        }

        @JavascriptInterface
        fun androidUrl(targetUrl: String) {
            NotificationReceiver.launchActivity(
                context,
                null,
                targetUrl
            )
        }

        @JavascriptInterface
        fun sendClick(buttonId: String?) {
            inAppMessageCallback?.inAppMessageClicked(inAppMessage, buttonId)
        }

        @JavascriptInterface
        fun close() {
            this@InAppMessageDialog.dismiss()
        }

        @JavascriptInterface
        fun setTags(tags: Array<TagItem>?) {
//            inAppMessageCallback?.sendTags(tags)
        }

        @JavascriptInterface
        fun iosUrl(targetUrl: String) {
        }

        @JavascriptInterface
        fun promptPushPermission() {
        }
    }

}