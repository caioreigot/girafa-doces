package com.github.caioreigot.girafadoces.presentation.signup

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.remote.auth.FirebaseAuthDataSource
import com.github.caioreigot.girafadoces.presentation.base.BaseActivity

class SignUpActivity : BaseActivity() {

    lateinit var rootView: RelativeLayout

    lateinit var fullNameET: EditText
    lateinit var emailET: EditText
    lateinit var deliveryAddressET: EditText
    lateinit var postalNumberET: EditText
    lateinit var passwordET: EditText
    lateinit var confirmPasswordET: EditText

    lateinit var viewFlipper: ViewFlipper
    lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val mViewModel: SignUpViewModel = SignUpViewModel.ViewModelFactory(
            FirebaseAuthDataSource()
        )
            .create(SignUpViewModel::class.java)

        //region Assignments
        rootView = findViewById(R.id.sign_up_root_view)

        fullNameET = findViewById(R.id.sign_up_full_name_et)
        emailET = findViewById(R.id.sign_up_email_et)
        deliveryAddressET = findViewById(R.id.sign_up_delivery_adress_et)
        postalNumberET = findViewById(R.id.sign_up_postal_number_et)
        passwordET = findViewById(R.id.sign_up_password_et)
        confirmPasswordET = findViewById(R.id.sign_up_confirm_password_et)

        viewFlipper = findViewById(R.id.sign_up_vf)
        signUpButton = findViewById(R.id.sign_up_btn)
        //endregion

        //region Listeners
        signUpButton.setOnClickListener {
            // Adding postal number with the address
            val deliveryAddressText = "${deliveryAddressET.text} - nº ${postalNumberET.text}"

            mViewModel.registerUser(
                fullName = fullNameET.text.toString(),
                email = emailET.text.toString(),
                deliveryAddress = deliveryAddressText,
                password = passwordET.text.toString(),
                passwordConfirm = confirmPasswordET.text.toString()
            )

            hideKeyboard()
        }

        confirmPasswordET.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND)  {
                    confirmPasswordET.clearFocus()
                    signUpButton.callOnClick()
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
        mViewModel.registrationMadeLiveData.observe(this, {
            it?.let {
                // TODO: Mostrar dialog customizado
                // Ao dar "Ok" no dialog, já logar no aplicativo
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
        //endregion
    }
}