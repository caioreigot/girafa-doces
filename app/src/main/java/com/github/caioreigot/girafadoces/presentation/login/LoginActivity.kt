package com.github.caioreigot.girafadoces.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.remote.auth.FirebaseAuthDataSource
import com.github.caioreigot.girafadoces.data.Singleton
import com.github.caioreigot.girafadoces.presentation.base.BaseActivity
import com.github.caioreigot.girafadoces.presentation.signup.SignUpActivity

class LoginActivity : BaseActivity() {

    private lateinit var rootView: RelativeLayout

    private lateinit var loginBtn: Button
    private lateinit var forgotPasswordBtn: Button
    private lateinit var signUpBtn: Button

    private lateinit var viewFlipper: ViewFlipper

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val mViewModel: LoginViewModel = LoginViewModel.ViewModelFactory(
            FirebaseAuthDataSource()
        )
            .create(LoginViewModel::class.java)

        //region Assignments
        rootView = findViewById(R.id.login_root_view)

        loginBtn = findViewById(R.id.login_btn)
        forgotPasswordBtn = findViewById(R.id.login_forgot_password_btn)
        signUpBtn = findViewById(R.id.login_sign_up_btn)

        viewFlipper = findViewById(R.id.login_view_flipper)

        emailET = findViewById(R.id.login_email_et)
        passwordET = findViewById(R.id.login_password_et)
        //endregion

        //region Listeners
        loginBtn.setOnClickListener {
            mViewModel.loginUser(
                emailET.text.toString(),
                passwordET.text.toString()
            )

            hideKeyboard()
        }

        forgotPasswordBtn.setOnClickListener {
            // TODO: Criar dialog customizado
        }

        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        passwordET.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND)  {
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
        mViewModel.loggedInLiveData.observe(this, {
            it?.let {
                Log.d("MY_DEBUG", Singleton.mFirebaseAuth.currentUser!!.uid)
            }
        })

        mViewModel.viewFlipperLiveData.observe(this, {
            it?.let { childToDisplay ->
                viewFlipper.displayedChild = childToDisplay
            }
        })

        mViewModel.errorMessageLiveData.observe(this, { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        })
        //endregion
    }
}