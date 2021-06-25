package com.github.caioreigot.girafadoces.ui.main.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.ui.main.admin.administrators.AdministratorsDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminPanelFragment : Fragment(R.layout.fragment_admin_panel) {

    private lateinit var administratorsBtnCV: CardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        administratorsBtnCV = view.findViewById(R.id.admin_administrators_btn_cv)

        administratorsBtnCV.setOnClickListener {
            val administratorsDialog = AdministratorsDialog()

            administratorsDialog.show(childFragmentManager, administratorsDialog.tag)
        }
    }
}