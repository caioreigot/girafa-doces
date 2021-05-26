package com.github.caioreigot.girafadoces.presentation.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.remote.auth.FirebaseAuthDataSource
import com.github.caioreigot.girafadoces.data.Singleton
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.presentation.base.BaseActivity
import com.github.caioreigot.girafadoces.presentation.signup.SignUpActivity

class LoginActivity : BaseActivity() {

    private lateinit var rootView: RelativeLayout

    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button

    private lateinit var forgotPasswordTV: TextView

    private lateinit var viewFlipper: ViewFlipper

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_GirafaDoces)
        setContentView(R.layout.activity_login)

        val mViewModel: LoginViewModel = LoginViewModel.ViewModelFactory(
            FirebaseAuthDataSource(),
            ResourcesProvider(this)
        )
            .create(LoginViewModel::class.java)

        //region Assignments
        rootView = findViewById(R.id.login_root_view)

        loginBtn = findViewById(R.id.login_btn)
        forgotPasswordTV = findViewById(R.id.login_forgot_password_btn)
        signUpBtn = findViewById(R.id.login_sign_up_btn)

        viewFlipper = findViewById(R.id.login_view_flipper)

        emailET = findViewById(R.id.login_email_et)
        passwordET = findViewById(R.id.login_password_et)
        //endregion

        //region Listeners
        loginBtn.setOnClickListener {
            mViewModel.loginUser(
                emailET.text.toString().trimEnd(),
                passwordET.text.toString()
            )

            hideKeyboard()
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
        mViewModel.loggedIn.observe(this, {
            it?.let {
                Log.d("MY_DEBUG", Singleton.mFirebaseAuth.currentUser!!.uid)
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
