package com.dengage.sdk.inappmessage

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.dengage.sdk.ImageDownloader
import com.dengage.sdk.R
import com.dengage.sdk.databinding.DialogInAppMessageBinding
import com.dengage.sdk.inappmessage.model.ContentPosition
import com.dengage.sdk.inappmessage.model.InAppMessage


/**
 * Created by Batuhan Coskun on 26 February 2021
 */
class InAppMessageDialog : DialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogInAppMessageBinding
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_in_app_message, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setOnCancelListener {
            inAppMessageDismissed()
        }

        inAppMessage = requireArguments().getSerializable(EXTRA_IN_APP_MESSAGE) as InAppMessage
        val contentParams = inAppMessage.data.content.params
        binding.tvInAppTitle.visibility = if (contentParams.showTitle) View.VISIBLE else View.GONE
        binding.tvInAppTitle.text = contentParams.title
        binding.tvInAppMessage.text = contentParams.message

        binding.cardInAppMessageImage.visibility = if (contentParams.showImage) View.VISIBLE else View.GONE
        if (contentParams.showImage && !contentParams.imageUrl.isNullOrEmpty()) {
            ImageDownloader(contentParams.imageUrl, object : ImageDownloader.OnImageLoaderListener {
                override fun onError(error: ImageDownloader.ImageError) {
                    binding.cardInAppMessageImage.visibility = View.GONE
                }

                override fun onComplete(bitmap: Bitmap) {
                    binding.ivInAppMessage.setImageBitmap(bitmap)
                }
            }).start()
        }

        val params = RelativeLayout.LayoutParams(MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.in_app_message_height))
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
        binding.vInAppMessage.layoutParams = params

        binding.vInAppMessageContainer.setOnClickListener(this)
        binding.vInAppMessage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.vInAppMessageContainer -> {
                inAppMessageDismissed()
            }
            R.id.vInAppMessage -> {
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
        binding.unbind()
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