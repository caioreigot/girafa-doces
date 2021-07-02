package com.github.caioreigot.girafadoces.ui.main.admin.administrators

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.User

class AdminInfoDialog(private val admin: User) : DialogFragment(R.layout.dialog_admin_info) {

    lateinit var nameTV: TextView
    lateinit var emailTV: TextView
    lateinit var deliveryAddressTV: TextView
    lateinit var phoneTV: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        nameTV = view.findViewById(R.id.admin_info_name_tv)
        emailTV = view.findViewById(R.id.admin_info_email_address_tv)
        deliveryAddressTV = view.findViewById(R.id.admin_info_delivery_address_tv)
        phoneTV = view.findViewById(R.id.admin_info_phone_tv)
        //endregion

        nameTV.text = admin.fullName
        emailTV.text = admin.email
        deliveryAddressTV.text = admin.deliveryAddress
        phoneTV.text = admin.phoneNumber
    }
}