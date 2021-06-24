package com.github.caioreigot.girafadoces.ui.main.admin.administrators

import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddAdminDialog : DialogFragment(R.layout.add_admin_dialog) {

    @Inject lateinit var administratorsVMFactory: AdministratorsViewModel.ViewModelFactory

    private val administratorsViewModel: AdministratorsViewModel by viewModels(
        { requireParentFragment() },
        { administratorsVMFactory }
    )

    /*private val adminPanelViewModel: AdminPanelViewModel by lazy {
        ViewModelProvider(requireParentFragment().viewModelStore, adminPanelVMFactory)
            .get(AdminPanelViewModel::class.java)
    }*/

    lateinit var viewFlipper: ViewFlipper
    lateinit var addAdminBtnCV: CardView

    lateinit var emailEditText: EditText

    lateinit var addAdminStatusMessage: TextView

    companion object {
        private const val VIEW_FLIPPER_ADD_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        viewFlipper = view.findViewById(R.id.add_admin_view_flipper)
        emailEditText = view.findViewById(R.id.add_admin_email_edit_text)
        addAdminBtnCV = view.findViewById(R.id.add_admin_btn_cv)
        addAdminStatusMessage = view.findViewById(R.id.add_admin_info_status)
        //endregion

        //region Listeners
        addAdminBtnCV.setOnClickListener {
            viewFlipper.displayedChild = VIEW_FLIPPER_PROGRESS_BAR

            val enteredEmail = emailEditText.text.toString().trimEnd()
            administratorsViewModel.addAdmin(enteredEmail)
        }
        //endregion

        //region Observers
        administratorsViewModel.addAdminErrorMessageLD.observe(viewLifecycleOwner, { message ->
            viewFlipper.displayedChild = VIEW_FLIPPER_ADD_BUTTON

            addAdminStatusMessage.text = message
            addAdminStatusMessage.visibility = View.VISIBLE
        })
        //endregion
    }
}