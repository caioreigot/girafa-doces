package com.github.caioreigot.girafadoces.ui.main.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.remote.DatabaseService
import com.github.caioreigot.girafadoces.ui.main.admin.administrators.AdministratorsDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminPanelFragment : Fragment() {

    //private val mViewModel: AdminPanelViewModel by viewModels()

    lateinit var administratorsBtn: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        administratorsBtn = view.findViewById(R.id.admin_administrators_btn_cv)

        administratorsBtn.setOnClickListener {
            val administratorsDialog = AdministratorsDialog(/*mViewModel*/)

            administratorsDialog.show(
                requireActivity().supportFragmentManager,
                administratorsDialog.tag
            )
        }
    }
}