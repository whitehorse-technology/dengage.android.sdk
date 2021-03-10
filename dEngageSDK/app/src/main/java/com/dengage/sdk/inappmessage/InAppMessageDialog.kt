package com.dengage.sdk.inappmessage

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.dengage.sdk.ImageDownloader
import com.dengage.sdk.R
import com.dengage.sdk.inappmessage.model.ContentPosition
import com.dengage.sdk.inappmessage.model.InAppMessage

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
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setOnCancelListener {
            inAppMessageDismissed()
        }

        val tvInAppTitle = view.findViewById<AppCompatTextView>(R.id.tvInAppTitle)
        val tvInAppMessage = view.findViewById<AppCompatTextView>(R.id.tvInAppMessage)
        val cardInAppMessageImage = view.findViewById<CardView>(R.id.cardInAppMessageImage)
        val ivInAppMessage = view.findViewById<AppCompatImageView>(R.id.ivInAppMessage)
        val cardInAppMessage = view.findViewById<CardView>(R.id.cardInAppMessage)
        val vInAppMessage = view.findViewById<RelativeLayout>(R.id.vInAppMessage)
        val vInAppMessageContainer = view.findViewById<RelativeLayout>(R.id.vInAppMessageContainer)

        inAppMessage = requireArguments().getSerializable(EXTRA_IN_APP_MESSAGE) as InAppMessage
        val contentParams = inAppMessage.data.content.params
        tvInAppTitle.visibility = if (contentParams.showTitle) View.VISIBLE else View.GONE
        tvInAppTitle.text = contentParams.title
        tvInAppMessage.text = contentParams.message

        // set colors
        if (!contentParams.backgroundColor.isNullOrEmpty() &&
                contentParams.backgroundColor.length == 6) {
            vInAppMessage.setBackgroundColor(Color.parseColor("#${contentParams.backgroundColor}"))
        }
        if (!contentParams.primaryColor.isNullOrEmpty() &&
                contentParams.primaryColor.length == 6) {
            tvInAppTitle.setTextColor(Color.parseColor("#${contentParams.primaryColor}"))
        }
        if (!contentParams.secondaryColor.isNullOrEmpty() &&
                contentParams.secondaryColor.length == 6) {
            tvInAppMessage.setTextColor(Color.parseColor("#${contentParams.secondaryColor}"))
        }

        if (contentParams.showImage && !contentParams.imageUrl.isNullOrEmpty()) {
            ImageDownloader(contentParams.imageUrl, object : ImageDownloader.OnImageLoaderListener {
                override fun onError(error: ImageDownloader.ImageError) {
                    cardInAppMessageImage.visibility = View.GONE
                }

                override fun onComplete(bitmap: Bitmap) {
                    cardInAppMessageImage.visibility = View.VISIBLE
                    ivInAppMessage.setImageBitmap(bitmap)
                }
            }).start()
        }

        val params = RelativeLayout.LayoutParams(
                MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.in_app_message_height)
        )
        val marginTop = resources.getDimensionPixelSize(R.dimen.in_app_message_margin_top)
        val marginBottom = resources.getDimensionPixelSize(R.dimen.in_app_message_margin_bottom)
        val marginStart = resources.getDimensionPixelSize(R.dimen.in_app_message_margin_start)
        val marginEnd = resources.getDimensionPixelSize(R.dimen.in_app_message_margin_end)
        params.setMargins(marginStart, marginTop, marginEnd, marginBottom)
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

        vInAppMessageContainer.setOnClickListener(this)
        cardInAppMessage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.vInAppMessageContainer -> {
                dismiss()
                inAppMessageDismissed()
            }
            R.id.cardInAppMessage -> {
                dismiss()
                inAppMessageClicked()
            }
        }
    }

    private fun inAppMessageClicked() {
        inAppMessageCallback?.inAppMessageClicked(inAppMessage)
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
        fun inAppMessageClicked(inAppMessage: InAppMessage)
        fun inAppMessageDismissed(inAppMessage: InAppMessage)
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

}