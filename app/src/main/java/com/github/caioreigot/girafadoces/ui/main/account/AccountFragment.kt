package com.github.caioreigot.girafadoces.ui.main.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.model.UserAccountField
import com.github.caioreigot.girafadoces.data.model.UserSingleton
import com.github.caioreigot.girafadoces.data.remote.DatabaseService
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AccountFragment : Fragment() {

    private lateinit var mViewModel: AccountViewModel

    private lateinit var loadingViewFlipper: ViewFlipper
    private lateinit var informationsViewGroup: ViewGroup

    private lateinit var nameTV: TextView
    private lateinit var addressTV: TextView
    private lateinit var emailTV: TextView
    private lateinit var phoneTV: TextView

    private lateinit var editAccountName: ViewGroup
    private lateinit var editAccountDeliveryAddress: ViewGroup
    private lateinit var editAccountPhone: ViewGroup

    private lateinit var signOutBtn: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel = AccountViewModel.ViewModelFactory(
            ResourcesProvider(requireContext()),
            DatabaseService()
        ).create(AccountViewModel::class.java)

        //region Assignments
        loadingViewFlipper = view.findViewById(R.id.account_loading_view_flipper)
        informationsViewGroup = view.findViewById(R.id.account_informations_view_group)

        nameTV = view.findViewById(R.id.account_user_name)
        addressTV = view.findViewById(R.id.account_user_address)
        emailTV = view.findViewById(R.id.account_user_email)
        phoneTV = view.findViewById(R.id.account_user_phone)

        editAccountName = view.findViewById(R.id.account_name_header_view_group)
        editAccountDeliveryAddress = view.findViewById(R.id.account_address_header_view_group)
        editAccountPhone = view.findViewById(R.id.account_phone_header_view_group)

        signOutBtn = view.findViewById(R.id.account_sign_out_btn)
        //endregion

        mViewModel.fetchUserAccountInformation()

        //region Listeners
        signOutBtn.setOnClickListener {
            Singleton.mAuth.signOut()
        }

        editAccountName
            .setOnClickListener(ChangeAccountFieldListener(
                activity,
                mViewModel,
                UserAccountField.NAME)
            )

        editAccountDeliveryAddress
            .setOnClickListener(ChangeAccountFieldListener(
                activity,
                mViewModel,
                UserAccountField.DELIVERY_ADDRESS)
            )

        editAccountPhone
            .setOnClickListener(ChangeAccountFieldListener(
                activity,
                mViewModel,
                UserAccountField.PHONE)
            )
        //endregion

        //region Observers
        mViewModel.userAccountInformationLD.observe(viewLifecycleOwner, {
            it?.let { user ->
                UserSingleton.set(user)

                nameTV.text = UserSingleton.fullName
                addressTV.text = UserSingleton.deliveryAddress
                emailTV.text = UserSingleton.email
                phoneTV.text = UserSingleton.phoneNumber

                informationsViewGroup.visibility = View.VISIBLE
            }
        })

        mViewModel.loadingViewFlipperLD.observe(viewLifecycleOwner, {
            it?.let { childToDisplay ->
                loadingViewFlipper.displayedChild = childToDisplay
            }
        })

        mViewModel.reloadInformationLD.observe(viewLifecycleOwner, {
            mViewModel.fetchUserAccountInformation()
        })
        //endregion
    }

    class ChangeAccountFieldListener(
        private val activity: FragmentActivity?,
        private val accountViewModel: AccountViewModel,
        private val fieldToChange: UserAccountField
        ) : View.OnClickListener
    {
        override fun onClick(v: View?) {
            activity?.supportFragmentManager?.let { supportFragmentManager ->
                val changeInfoDialog = ChangeInfoDialog(
                    accountViewModel,
                    fieldToChange,
                    ResourcesProvider(activity)
                )

                changeInfoDialog.show(supportFragmentManager, changeInfoDialog.tag)
            }
        }
    }
}