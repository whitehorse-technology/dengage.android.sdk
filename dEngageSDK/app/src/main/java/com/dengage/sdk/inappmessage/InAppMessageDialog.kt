package com.dengage.sdk.inappmessage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.dengage.sdk.R
import com.dengage.sdk.databinding.DialogInAppMessageBinding
import com.dengage.sdk.inappmessage.model.InAppMessage

/**
 * Created by Batuhan Coskun on 26 February 2021
 */
class InAppMessageDialog : DialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogInAppMessageBinding
    private lateinit var inAppMessage: InAppMessage

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
        binding.tvInAppTitle.visibility = if (inAppMessage.data.content.params.showTitle) View.VISIBLE else View.GONE
        binding.ivInAppMessage.visibility = if (inAppMessage.data.content.params.showImage) View.VISIBLE else View.GONE
        binding.tvInAppTitle.text = inAppMessage.data.content.params.title
        binding.tvInAppMessage.text = inAppMessage.data.content.params.message

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
        // todo action in app click
    }

    private fun inAppMessageDismissed() {
        // todo action in app dismiss
    }

    override fun onDestroy() {
        binding.unbind()
        super.onDestroy()
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