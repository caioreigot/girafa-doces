package com.github.caioreigot.girafadoces.presentation.login

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ViewFlipper
import androidx.fragment.app.DialogFragment
import com.github.caioreigot.girafadoces.R

class ForgotPasswordDialog(private val loginViewModel: LoginViewModel) : DialogFragment() {

    lateinit var forgotPasswordViewFlipper: ViewFlipper
    lateinit var forgotPasswordBackBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.forgot_password_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forgotPasswordViewFlipper = dialog!!.findViewById(R.id.forgot_password_dialog_view_flipper)
        forgotPasswordBackBtn = dialog!!.findViewById(R.id.forgot_password_dialog_back_btn)

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)

        val emailET = view.findViewById<EditText>(R.id.forgot_password_dialog_email_et)
        val sendBtn = view.findViewById<Button>(R.id.forgot_password_send_btn)

        //forgotPasswordDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        sendBtn.setOnClickListener {
            loginViewModel.sendPasswordResetEmail(emailET.text.toString())
        }

        forgotPasswordBackBtn.setOnClickListener {
            dialog?.dismiss()
        }

        loginViewModel.forgotPasswordBtnViewFlipper.observe(this, {
            it?.let { childToDisplay ->
                forgotPasswordViewFlipper.displayedChild = childToDisplay
            }
        })
    }

}