package com.github.caioreigot.girafadoces.presentation.signup

import android.os.Bundle
import android.text.InputType.*
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.remote.auth.FirebaseAuthDataSource
import com.github.caioreigot.girafadoces.presentation.base.BaseActivity


class SignUpActivity : BaseActivity() {

    lateinit var rootView: RelativeLayout

    lateinit var fullNameET: EditText
    lateinit var emailET: EditText
    lateinit var phoneDDD: EditText
    lateinit var phoneNumber: EditText
    lateinit var deliveryAddressET: EditText
    lateinit var postalNumberET: EditText

    lateinit var passwordET: EditText
    lateinit var passwordVisibilityBtn: Button
    lateinit var confirmPasswordET: EditText
    lateinit var confirmPasswordVisibilityBtn: Button

    lateinit var viewFlipper: ViewFlipper
    lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SignUpScreen)
        setContentView(R.layout.activity_sign_up)

        val mViewModel: SignUpViewModel = SignUpViewModel.ViewModelFactory(
            FirebaseAuthDataSource(),
            ResourcesProvider(this)
        )
            .create(SignUpViewModel::class.java)

        //region Assignments
        rootView = findViewById(R.id.sign_up_root_view)

        fullNameET = findViewById(R.id.sign_up_full_name_et)
        emailET = findViewById(R.id.sign_up_email_et)
        phoneDDD = findViewById(R.id.sign_up_phone_ddd_et)
        phoneNumber = findViewById(R.id.sign_up_phone_et)
        deliveryAddressET = findViewById(R.id.sign_up_delivery_adress_et)
        postalNumberET = findViewById(R.id.sign_up_postal_number_et)
        passwordET = findViewById(R.id.sign_up_password_et)
        passwordVisibilityBtn = findViewById(R.id.sign_up_password_visibility_button)
        confirmPasswordET = findViewById(R.id.sign_up_confirm_password_et)
        confirmPasswordVisibilityBtn = findViewById(R.id.sign_up_confirm_password_visibility_button)

        viewFlipper = findViewById(R.id.sign_up_vf)
        signUpButton = findViewById(R.id.sign_up_btn)
        //endregion

        //region Listeners
        signUpButton.setOnClickListener {
            mViewModel.registerUser(
                fullName = fullNameET.text.toString().trimEnd(),
                email = emailET.text.toString().trimEnd(),
                phoneDDD = phoneDDD.text.toString(),
                phoneNumber = phoneNumber.text.toString(),
                deliveryAddress = deliveryAddressET.text.toString(),
                postalNumber = postalNumberET.text.toString(),
                password = passwordET.text.toString(),
                passwordConfirm = confirmPasswordET.text.toString()
            )

            hideKeyboard()
        }

        passwordVisibilityBtn
            .setOnClickListener(PasswordVisibilityButtonListener(passwordET))
        (passwordVisibilityBtn.parent as LinearLayout)
            .setOnClickListener { passwordVisibilityBtn.callOnClick() }

        confirmPasswordVisibilityBtn
            .setOnClickListener(PasswordVisibilityButtonListener(confirmPasswordET))
        (confirmPasswordVisibilityBtn.parent as LinearLayout)
            .setOnClickListener { confirmPasswordVisibilityBtn.callOnClick() }

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
        mViewModel.registrationMade.observe(this, {
            it?.let {
                createMessageDialog(
                    this,
                    MessageType.SUCCESSFUL,
                    getString(R.string.dialog_successful_title),
                    getString(R.string.signup_success_message)
                ) { finish() }.show()
            }
        })

        mViewModel.registerBtnViewFlipper.observe(this, {
            it?.let { childToDisplay ->
                viewFlipper.displayedChild = childToDisplay
            }
        })

        mViewModel.errorMessage.observe(this, {
            it?.let { errorMessage ->
                createMessageDialog(
                    this,
                    MessageType.ERROR,
                    getString(R.string.dialog_error_title),
                    errorMessage,
                    null
                ).show()
            }
        })
        //endregion
    }

    class PasswordVisibilityButtonListener(var editText: EditText) : View.OnClickListener {
        override fun onClick(v: View?) {
            if (editText.inputType == TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD) {
                val cursorPosition = editText.selectionStart

                editText.inputType = (TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                editText.setSelection(cursorPosition)

                v?.setBackgroundResource(R.drawable.ic_baseline_visibility_24)
            } else { // Not visible
                val cursorPosition = editText.selectionStart

                editText.inputType = (TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD)
                editText.setSelection(cursorPosition)

                v?.setBackgroundResource(R.drawable.ic_baseline_visibility_off_24)
            }
        }
    }
}