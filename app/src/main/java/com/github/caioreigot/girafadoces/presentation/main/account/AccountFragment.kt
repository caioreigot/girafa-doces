package com.github.caioreigot.girafadoces.presentation.main.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.local.Preferences
import com.github.caioreigot.girafadoces.data.model.UserSingleton
import com.github.caioreigot.girafadoces.data.remote.auth.FirebaseAuthDataSource
import com.github.caioreigot.girafadoces.data.remote.database.FirebaseDatabaseDataSource
import com.github.caioreigot.girafadoces.presentation.login.LoginActivity
import com.github.caioreigot.girafadoces.presentation.login.LoginViewModel

class AccountFragment : Fragment() {

    lateinit var nameTV: TextView
    lateinit var addressTV: TextView
    lateinit var emailTV: TextView
    lateinit var phoneTV: TextView

    lateinit var editAccountName: Button
    lateinit var editAccountDeliveryAddress: Button
    lateinit var editAccountPhone: Button
    lateinit var signOutBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mViewModel: AccountViewModel = AccountViewModel.ViewModelFactory(
            Preferences(requireContext())
        ).create(AccountViewModel::class.java)

        //region Assignments
        nameTV = view.findViewById(R.id.account_user_name)
        addressTV = view.findViewById(R.id.account_user_address)
        emailTV = view.findViewById(R.id.account_user_email)
        phoneTV = view.findViewById(R.id.account_user_phone)

        editAccountName = view.findViewById(R.id.account_name_edit_button)
        editAccountDeliveryAddress = view.findViewById(R.id.account_address_edit_button)
        editAccountPhone = view.findViewById(R.id.account_phone_edit_button)
        signOutBtn = view.findViewById(R.id.account_sign_out)

        nameTV.text = UserSingleton.fullName
        addressTV.text = UserSingleton.deliveryAddress
        emailTV.text = UserSingleton.email
        phoneTV.text = UserSingleton.phoneNumber
        //endregion

        //region Listeners
        signOutBtn.setOnClickListener {
            Singleton.mFirebaseAuth.signOut()
        }

        Singleton.mFirebaseAuth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                Preferences(requireContext()).clearPreferences()
                startActivity(intent)
                activity?.finish()
            }
        }
        //endregion
    }
}