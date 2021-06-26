package com.github.caioreigot.girafadoces.ui.main.account

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.PhoneNumberWatcher
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.UserAccountField
import com.github.caioreigot.girafadoces.ui.main.BottomNavActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangeInfoDialog(
    private val accountFieldToChange: UserAccountField,
) : DialogFragment() {

    @Inject
    lateinit var resProvider: ResourcesProvider

    @Inject
    lateinit var accountVMFactory: AccountViewModel.Factory

    private val accountViewModel: AccountViewModel by viewModels(
        { requireParentFragment() },
        { accountVMFactory }
    )

    private lateinit var changeAccountInfoHeader: TextView
    private lateinit var validationStatus: TextView

    private lateinit var changeInfoEditText: EditText

    private lateinit var saveButtonBtnCV: CardView
    private lateinit var viewFlipper: ViewFlipper

    companion object {
        private const val VIEW_FLIPPER_SAVE_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

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
        saveButtonBtnCV = view.findViewById(R.id.change_account_info_save_btn_cv)
        viewFlipper = view.findViewById(R.id.change_account_info_view_flipper)

        when (accountFieldToChange) {
            UserAccountField.EMAIL -> {
                changeAccountInfoHeader.text = resProvider.getString(R.string.change_email_header)
                changeInfoEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

                saveButtonBtnCV.setOnClickListener {
                    viewFlipper.displayedChild = VIEW_FLIPPER_PROGRESS_BAR

                    accountViewModel.changeAccountField(
                        UserAccountField.EMAIL, changeInfoEditText.text.toString()
                    )
                }
            }

            UserAccountField.NAME -> {
                changeAccountInfoHeader.text = resProvider.getString(R.string.change_name_header)
                changeInfoEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS

                saveButtonBtnCV.setOnClickListener {
                    viewFlipper.displayedChild = VIEW_FLIPPER_PROGRESS_BAR

                    accountViewModel.changeAccountField(
                        UserAccountField.NAME, changeInfoEditText.text.toString()
                    )
                }
            }

            UserAccountField.DELIVERY_ADDRESS -> {
                changeAccountInfoHeader.text = resProvider.getString(R.string.change_delivery_address_header)
                changeInfoEditText.inputType = InputType.TYPE_CLASS_TEXT

                saveButtonBtnCV.setOnClickListener {
                    viewFlipper.displayedChild = VIEW_FLIPPER_PROGRESS_BAR

                    accountViewModel.changeAccountField(
                        UserAccountField.DELIVERY_ADDRESS, changeInfoEditText.text.toString()
                    )
                }
            }

            UserAccountField.PHONE -> {
                changeAccountInfoHeader.text = resProvider.getString(R.string.change_phone_header)
                changeInfoEditText.inputType = InputType.TYPE_CLASS_NUMBER

                changeInfoEditText.addTextChangedListener(PhoneNumberWatcher(changeInfoEditText))

                saveButtonBtnCV.setOnClickListener {
                    viewFlipper.displayedChild = VIEW_FLIPPER_PROGRESS_BAR

                    accountViewModel.changeAccountField(
                        UserAccountField.PHONE, changeInfoEditText.text.toString()
                    )
                }
            }
        }

        accountViewModel.successMessageLD.observe(viewLifecycleOwner, {
            it?.let {
                dialog?.dismiss()

                (activity as BottomNavActivity).showMessageDialog(
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

                viewFlipper.displayedChild = VIEW_FLIPPER_SAVE_BUTTON
            }
        })
    }
}