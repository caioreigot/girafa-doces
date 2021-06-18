package com.github.caioreigot.girafadoces.presentation.main.admin.administrators

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.presentation.main.admin.AdminViewModel

class AddAdminDialog(private val adminViewModel: AdminViewModel) : DialogFragment() {

    lateinit var emailEditText: EditText
    lateinit var addAdminBtnCV: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_admin_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        emailEditText = view.findViewById(R.id.add_admin_email_edit_text)
        addAdminBtnCV = view.findViewById(R.id.add_admin_btn_cv)
        //endregion

        addAdminBtnCV.setOnClickListener {
            val enteredEmail = emailEditText.text.toString().trimEnd()
            adminViewModel.addAdmin(enteredEmail)
        }
    }
}