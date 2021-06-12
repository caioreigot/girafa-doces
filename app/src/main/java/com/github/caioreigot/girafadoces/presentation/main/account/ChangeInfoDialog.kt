package com.github.caioreigot.girafadoces.presentation.main.account

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.Utils
import com.github.caioreigot.girafadoces.data.Utils.Companion.putCharBefore
import com.github.caioreigot.girafadoces.data.Utils.Companion.removeLastWord
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.UserAccountField
import com.github.caioreigot.girafadoces.presentation.main.MainActivity

class ChangeInfoDialog(
    private val accountViewModel: AccountViewModel,
    private val accountFieldToChange: UserAccountField,
    private val resProvider: ResourcesProvider
) : DialogFragment() {

    private lateinit var changeAccountInfoHeader: TextView
    private lateinit var validationStatus: TextView

    private lateinit var changeInfoEditText: EditText

    private lateinit var saveButtonCV: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.change_account_info_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        changeAccountInfoHeader = view.findViewById(R.id.change_account_info_header_tv)
        changeInfoEditText = view.findViewById(R.id.change_account_info_edit_text)
        validationStatus = view.findViewById(R.id.change_account_info_status)
        saveButtonCV = view.findViewById(R.id.change_account_info_save_btn_cv)

        when (accountFieldToChange) {
            UserAccountField.EMAIL -> {
                changeAccountInfoHeader.text = resProvider.getString(R.string.change_email_header)
                changeInfoEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

                saveButtonCV.setOnClickListener {
                    accountViewModel.changeAccountField(
                        UserAccountField.EMAIL, changeInfoEditText.text.toString()
                    )
                }
            }

            UserAccountField.NAME -> {
                changeAccountInfoHeader.text = resProvider.getString(R.string.change_name_header)
                changeInfoEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS

                saveButtonCV.setOnClickListener {
                    accountViewModel.changeAccountField(
                        UserAccountField.NAME, changeInfoEditText.text.toString()
                    )
                }
            }

            UserAccountField.DELIVERY_ADDRESS -> {
                changeAccountInfoHeader.text = resProvider.getString(R.string.change_delivery_address_header)
                changeInfoEditText.inputType = InputType.TYPE_CLASS_TEXT

                saveButtonCV.setOnClickListener {
                    accountViewModel.changeAccountField(
                        UserAccountField.DELIVERY_ADDRESS, changeInfoEditText.text.toString()
                    )
                }
            }

            UserAccountField.PHONE -> {
                changeAccountInfoHeader.text = resProvider.getString(R.string.change_phone_header)
                changeInfoEditText.inputType = InputType.TYPE_CLASS_NUMBER

                changeInfoEditText.addTextChangedListener(Utils.PhoneNumberWatcher(changeInfoEditText))

                saveButtonCV.setOnClickListener {
                    accountViewModel.changeAccountField(
                        UserAccountField.PHONE, changeInfoEditText.text.toString()
                    )
                }
            }
        }

        accountViewModel.successMessageLD.observe(viewLifecycleOwner, {
            it?.let {
                dialog?.dismiss()

                (activity as MainActivity).showMessageDialog(
                    MessageType.SUCCESSFUL,
                    R.string.dialog_successful_title,
                    resProvider.getString(R.string.change_made_successfully),
                    { accountViewModel.reloadInformation() }
                )
            }
        })

        accountViewModel.errorMessageLD.observe(viewLifecycleOwner, {
            it?.let { message ->
                validationStatus.text = message
                validationStatus.visibility = View.VISIBLE
            }
        })
    }
}