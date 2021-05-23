package com.github.caioreigot.girafadoces.presentation.signup

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ViewFlipper
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.remote.auth.FirebaseAuthDataSource
import com.github.caioreigot.girafadoces.presentation.base.BaseActivity

class SignUpActivity : BaseActivity() {

    lateinit var fullNameEditText: EditText
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var confirmPasswordEditText: EditText
    lateinit var viewFlipper: ViewFlipper
    lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val mViewModel: SignUpViewModel = SignUpViewModel.ViewModelFactory(
            FirebaseAuthDataSource()
        )
            .create(SignUpViewModel::class.java)

        // Assignments
        fullNameEditText = findViewById(R.id.sign_up_full_name_edit_text)
        emailEditText = findViewById(R.id.sign_up_email_edit_text)
        passwordEditText = findViewById(R.id.sign_up_password_edit_text)
        confirmPasswordEditText = findViewById(R.id.sign_up_confirm_password_edit_text)
        viewFlipper = findViewById(R.id.sign_up_view_flipper)
        signUpButton = findViewById(R.id.sign_up_btn)

        signUpButton.setOnClickListener {
            mViewModel.registerUser(
                fullName = fullNameEditText.text.toString(),
                email = emailEditText.text.toString(),
                password = passwordEditText.text.toString(),
                passwordConfirm = confirmPasswordEditText.text.toString()
            )

            hideKeyboard()
        }

        // Observers
        mViewModel.registrationMadeLiveData.observe(this, {
            it?.let {

            }
        })

        mViewModel.viewFlipperLiveData.observe(this, {
            it?.let { childToDisplay ->
                viewFlipper.displayedChild = childToDisplay
            }
        })

        mViewModel.errorMessageLiveData.observe(this, {
            it?.let { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }
}