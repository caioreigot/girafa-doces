package com.github.caioreigot.girafadoces.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.viewModels
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.UserSingleton
import com.github.caioreigot.girafadoces.ui.base.BaseActivity
import com.github.caioreigot.girafadoces.ui.main.BottomNavActivity
import com.github.caioreigot.girafadoces.ui.signup.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var rootView: RelativeLayout

    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button

    private lateinit var forgotPasswordTV: TextView

    private lateinit var viewFlipper: ViewFlipper

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    private lateinit var keepConnectedCB: CheckBox
    private lateinit var keepConnectedTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_GirafaDoces)
        setContentView(R.layout.activity_login)

        loginViewModel.searchRememberedAccount()

        //region Assignments
        rootView = findViewById(R.id.login_root_view)

        loginBtn = findViewById(R.id.login_btn)
        forgotPasswordTV = findViewById(R.id.login_forgot_password_btn)
        signUpBtn = findViewById(R.id.login_sign_up_btn)

        viewFlipper = findViewById(R.id.login_view_flipper)

        emailET = findViewById(R.id.login_email_et)
        passwordET = findViewById(R.id.login_password_et)

        keepConnectedCB = findViewById(R.id.login_keep_connected_cb)
        keepConnectedTV = findViewById(R.id.login_keep_connected_tv)
        //endregion

        //region Listeners
        loginBtn.setOnClickListener {
            loginViewModel.loginUser(
                emailET.text.toString().trimEnd(),
                passwordET.text.toString()
            )

            hideKeyboard()
        }

        keepConnectedTV.setOnClickListener {
            keepConnectedCB.isChecked = !keepConnectedCB.isChecked
        }

        forgotPasswordTV.setOnClickListener {
            /*val forgotPasswordDialog = Dialog(this, R.style.full_screen_dialog)*/
            val forgotPasswordDialog = ForgotPasswordDialog()
            forgotPasswordDialog.show(supportFragmentManager, forgotPasswordDialog.tag)
        }

        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        passwordET.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    passwordET.clearFocus()
                    loginBtn.callOnClick()
                    return true
                }

                return false
            }
        })

        rootView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hideKeyboard()
        }
        //endregion

        // Observers
        with (loginViewModel) {
            val thisActivity = this@LoginActivity

            loggedUserInformationLD.observe(thisActivity, { loggedUserAndPassword ->
                loggedUserAndPassword?.let { (loggedUser, password) ->
                    loggedUser?.let {
                        UserSingleton.set(loggedUser)

                        if (keepConnectedCB.isChecked)
                            rememberAccount(loggedUser.email, password)

                        val intent = Intent(thisActivity, BottomNavActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            })

            loginBtnViewFlipperLD.observe(thisActivity, {
                it?.let { childToDisplay ->
                    viewFlipper.displayedChild = childToDisplay
                }
            })

            errorMessageLD.observe(thisActivity, { errorMessage ->
                createMessageDialog(
                    thisActivity,
                    MessageType.ERROR,
                    getString(R.string.dialog_error_title),
                    errorMessage,
                    null
                ).show()
            })

            resetPasswordMessageLD.observe(thisActivity, { (messageType, message) ->
                createMessageDialog(
                    thisActivity,
                    messageType,
                    if (messageType == MessageType.ERROR) getString(R.string.dialog_error_title)
                    else getString(R.string.dialog_successful_title),
                    message,
                    null
                ).show()
            })
        }
    }
}
