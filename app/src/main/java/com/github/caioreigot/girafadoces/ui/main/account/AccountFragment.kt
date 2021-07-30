package com.github.caioreigot.girafadoces.ui.main.account

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.model.UserAccountField
import com.github.caioreigot.girafadoces.data.model.UserSingleton
import com.github.caioreigot.girafadoces.ui.main.BottomNavActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {

    @Inject
    lateinit var accountVMFactory: AccountViewModel.Factory

    private val accountViewModel: AccountViewModel by viewModels(
        { this },
        { accountVMFactory }
    )

    private lateinit var loadingViewFlipper: ViewFlipper
    private lateinit var informationViewGroup: ViewGroup

    private lateinit var nameTV: TextView
    private lateinit var addressTV: TextView
    private lateinit var emailTV: TextView
    private lateinit var phoneTV: TextView

    private lateinit var editAccountName: ViewGroup
    private lateinit var editAccountDeliveryAddress: ViewGroup
    private lateinit var editAccountPhone: ViewGroup

    private lateinit var signOutBtn: FloatingActionButton
    private var mConfirmSignOutDialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        loadingViewFlipper = view.findViewById(R.id.account_loading_view_flipper)
        informationViewGroup = view.findViewById(R.id.account_information_view_group)

        nameTV = view.findViewById(R.id.account_user_name)
        addressTV = view.findViewById(R.id.account_user_address)
        emailTV = view.findViewById(R.id.account_user_email)
        phoneTV = view.findViewById(R.id.account_user_phone)

        editAccountName = view.findViewById(R.id.account_name_header_view_group)
        editAccountDeliveryAddress = view.findViewById(R.id.account_address_header_view_group)
        editAccountPhone = view.findViewById(R.id.account_phone_header_view_group)

        signOutBtn = view.findViewById(R.id.account_sign_out_btn)
        //endregion

        accountViewModel.fetchUserAccountInformation()

        //region Listeners
        signOutBtn.setOnClickListener {
            val bottomNavActivity = (activity as BottomNavActivity)

            mConfirmSignOutDialog = bottomNavActivity.showMessageDialog(
                MessageType.CONFIRMATION,
                R.string.dialog_confirmation_title,
                R.string.account_sign_out_dialog_message,
                { Singleton.AUTH.signOut() }
            )

            mConfirmSignOutDialog?.show()
        }

        editAccountName
            .setOnClickListener(ChangeAccountFieldListener(
                childFragmentManager,
                UserAccountField.NAME
            )
        )

        editAccountDeliveryAddress
            .setOnClickListener(ChangeAccountFieldListener(
                childFragmentManager,
                UserAccountField.DELIVERY_ADDRESS
            )
        )

        editAccountPhone
            .setOnClickListener(ChangeAccountFieldListener(
                childFragmentManager,
                UserAccountField.PHONE
            )
        )
        //endregion

        //region Observers
        accountViewModel.userAccountInformation.observe(viewLifecycleOwner, {
            it?.let { user ->
                UserSingleton.set(user)

                nameTV.text = UserSingleton.fullName
                addressTV.text = UserSingleton.deliveryAddress
                emailTV.text = UserSingleton.email
                phoneTV.text = UserSingleton.phoneNumber

                informationViewGroup.visibility = View.VISIBLE
            }
        })

        accountViewModel.loadingViewFlipper.observe(viewLifecycleOwner, {
            it?.let { childToDisplay ->
                loadingViewFlipper.displayedChild = childToDisplay
            }
        })

        accountViewModel.reloadInformation.observe(viewLifecycleOwner, {
            accountViewModel.fetchUserAccountInformation()
        })
        //endregion
    }

    class ChangeAccountFieldListener(
        private val fragmentManager: FragmentManager,
        private val fieldToChange: UserAccountField
    ) : View.OnClickListener {
        override fun onClick(v: View?) {
            val changeInfoDialog = ChangeInfoDialog(fieldToChange)
            changeInfoDialog.show(fragmentManager, changeInfoDialog.tag)
        }
    }

    override fun onPause() {
        super.onPause()

        mConfirmSignOutDialog?.dismiss()
    }
}