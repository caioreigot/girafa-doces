package com.github.caioreigot.girafadoces.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.remote.auth.FirebaseAuthDataSource
import com.github.caioreigot.girafadoces.data.local.Preferences
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.UserSingleton
import com.github.caioreigot.girafadoces.data.remote.database.FirebaseDatabaseDataSource
import com.github.caioreigot.girafadoces.presentation.base.BaseActivity
import com.github.caioreigot.girafadoces.presentation.main.MainActivity
import com.github.caioreigot.girafadoces.presentation.signup.SignUpActivity

class LoginActivity : BaseActivity() {

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

        val mViewModel: LoginViewModel = LoginViewModel.ViewModelFactory(
            FirebaseAuthDataSource(),
            FirebaseDatabaseDataSource(),
            ResourcesProvider(this),
            Preferences(this)
        )
            .create(LoginViewModel::class.java)

        mViewModel.searchRememberedAccount()

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
            mViewModel.loginUser(
                emailET.text.toString().trimEnd(),
                passwordET.text.toString()
            )

            hideKeyboard()
        }

        keepConnectedTV.setOnClickListener {
            keepConnectedCB.isChecked = !keepConnectedCB.isChecked
        }

        forgotPasswordTV.setOnClickListener {
            //val forgotPasswordDialog = Dialog(this, R.style.full_screen_dialog)
            val forgotPasswordDialog = ForgotPasswordDialog(mViewModel)
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

        //region Observers
        mViewModel.loggedUserInformation.observe(this, { (loggedUser, password) ->
            loggedUser?.let {
                UserSingleton.set(loggedUser)

                if (keepConnectedCB.isChecked)
                    mViewModel.rememberAccount(loggedUser.email, password)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        mViewModel.loginBtnViewFlipper.observe(this, {
            it?.let { childToDisplay ->
                viewFlipper.displayedChild = childToDisplay
            }
        })

        mViewModel.errorMessage.observe(this, { errorMessage ->
            createMessageDialog(
                this,
                MessageType.ERROR,
                getString(R.string.dialog_error_title),
                errorMessage,
                null
            ).show()
        })

        mViewModel.resetPasswordMessage.observe(this, { (messageType, message) ->
            createMessageDialog(
                this,
                messageType,
                if (messageType == MessageType.ERROR) getString(R.string.dialog_error_title)
                else getString(R.string.dialog_successful_title),
                message,
                null
            ).show()
        })
        //endregion
    }
}
