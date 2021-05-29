package com.github.caioreigot.girafadoces.presentation.base

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Message
import android.util.TypedValue
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.MessageType

open class BaseActivity : AppCompatActivity() {

    protected fun hideKeyboard() {
        val inputMethodManager: InputMethodManager = getSystemService(
            INPUT_METHOD_SERVICE
        ) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    protected fun createMessageDialog(
        context: Context,
        messageType: MessageType,
        header: String,
        content: String,
        onClickListener: (() -> Unit)?
    ): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.message_dialog)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        /*
        val width = (resources.displayMetrics.widthPixels * 0.70).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
         */

        val dialogImage = dialog.findViewById<ImageView>(R.id.message_dialog_image_iv)
        val dialogTitleTv = dialog.findViewById<TextView>(R.id.message_dialog_title_tv)
        val dialogContentTv = dialog.findViewById<TextView>(R.id.message_dialog_content_tv)
        val dialogPositiveBtn = dialog.findViewById<Button>(R.id.message_dialog_positive_btn)

        when (messageType) {
            MessageType.SUCCESSFUL ->
                dialogImage.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)

            MessageType.ERROR ->
                dialogImage.setImageResource(R.drawable.ic_baseline_error_outline_24)
        }

        dialogTitleTv.text = header
        dialogContentTv.text = content

        dialogPositiveBtn.setOnClickListener{
            onClickListener?.invoke()
            dialog.dismiss()
        }

        return dialog
    }

}