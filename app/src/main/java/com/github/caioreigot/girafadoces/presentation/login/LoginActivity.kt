package com.github.caioreigot.girafadoces.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ViewFlipper
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.remote.auth.FirebaseAuthSource
import com.github.caioreigot.girafadoces.presentation.base.BaseActivity
import com.github.caioreigot.girafadoces.presentation.signup.SignUpActivity

class LoginActivity : BaseActivity() {

    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Assignments
        loginButton = findViewById(R.id.login_btn)
        signUpButton = findViewById(R.id.login_sign_up_btn)
        viewFlipper = findViewById(R.id.login_view_flipper)
        emailEditText = findViewById(R.id.login_email_edit_text)
        passwordEditText = findViewById(R.id.login_password_edit_text)

        val mViewModel: LoginViewModel = LoginViewModel.ViewModelFactory(
            FirebaseAuthSource()
        )
            .create(LoginViewModel::class.java)

        loginButton.setOnClickListener {
            mViewModel.loginUser(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )

            hideKeyboard()
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Observers
        mViewModel.loginStatusLiveData.observe(this, {
            it?.let {

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
    }
}