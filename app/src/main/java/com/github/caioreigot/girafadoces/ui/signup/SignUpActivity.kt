package com.github.caioreigot.girafadoces.ui.signup

import android.os.Bundle
import android.text.InputType.*
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.viewModels
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.PhoneNumberWatcher
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BaseActivity() {

    private val signUpViewModel: SignUpViewModel by viewModels()

    private lateinit var rootView: RelativeLayout

    private lateinit var fullNameET: EditText
    private lateinit var emailET: EditText
    private lateinit var phoneNumberET: EditText
    private lateinit var deliveryAddressET: EditText
    private lateinit var postalNumberET: EditText

    private lateinit var passwordET: EditText
    private lateinit var passwordVisibilityBtn: LinearLayout
    private lateinit var confirmPasswordET: EditText
    private lateinit var confirmPasswordVisibilityBtn: LinearLayout

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SignUpScreen)
        setContentView(R.layout.activity_sign_up)

        //region Assignments
        rootView = findViewById(R.id.sign_up_root_view)

        fullNameET = findViewById(R.id.sign_up_full_name_et)
        emailET = findViewById(R.id.sign_up_email_et)
        phoneNumberET = findViewById(R.id.sign_up_phone_et)
        deliveryAddressET = findViewById(R.id.sign_up_delivery_adress_et)
        postalNumberET = findViewById(R.id.sign_up_postal_number_et)
        passwordET = findViewById(R.id.sign_up_password_et)
        passwordVisibilityBtn = findViewById(R.id.sign_up_password_visibility_btn_la)
        confirmPasswordET = findViewById(R.id.sign_up_confirm_password_et)
        confirmPasswordVisibilityBtn = findViewById(R.id.sign_up_confirm_password_visibility_btn_la)

        viewFlipper = findViewById(R.id.sign_up_vf)
        signUpButton = findViewById(R.id.sign_up_btn)
        //endregion

        //region Listeners
        signUpButton.setOnClickListener {
            signUpViewModel.registerUser(
                fullName = fullNameET.text.toString().trimEnd(),
                email = emailET.text.toString().trimEnd(),
                phoneNumber = phoneNumberET.text.toString(),
                deliveryAddress = deliveryAddressET.text.toString(),
                postalNumber = postalNumberET.text.toString(),
                password = passwordET.text.toString(),
                passwordConfirm = confirmPasswordET.text.toString()
            )

            hideKeyboard()
        }

        phoneNumberET.addTextChangedListener(PhoneNumberWatcher(phoneNumberET))

        passwordVisibilityBtn.setOnClickListener(PasswordVisibilityButtonListener(passwordET))

        confirmPasswordVisibilityBtn.setOnClickListener(
            PasswordVisibilityButtonListener(confirmPasswordET)
        )

        confirmPasswordET.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
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
        signUpViewModel.registrationMadeLD.observe(this, {
            it?.let {
                createMessageDialog(
                    this,
                    MessageType.SUCCESSFUL,
                    getString(R.string.dialog_successful_title),
                    getString(R.string.signup_success_message)
                ) { finish() }.show()
            }
        })

        signUpViewModel.registerBtnViewFlipperLD.observe(this, {
            it?.let { childToDisplay ->
                viewFlipper.displayedChild = childToDisplay
            }
        })

        signUpViewModel.errorMessageLD.observe(this, {
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

                ((v as LinearLayout?)?.getChildAt(0) as ImageView?)
                    ?.setImageResource(R.drawable.ic_baseline_visibility_24)
            } else { // Not visible
                val cursorPosition = editText.selectionStart

                editText.inputType = (TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD)
                editText.setSelection(cursorPosition)

                ((v as LinearLayout?)?.getChildAt(0) as ImageView?)
                    ?.setImageResource(R.drawable.ic_baseline_visibility_off_24)
            }
        }
    }
}