package com.github.caioreigot.girafadoces.presentation.main.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.remote.database.DatabaseDataSource
import com.github.caioreigot.girafadoces.presentation.main.admin.administrators.AdministratorsDialog
import com.github.caioreigot.girafadoces.presentation.main.menu.MenuViewModel

class AdminFragment : Fragment() {

    private lateinit var mViewModel: AdminViewModel

    lateinit var administratorsBtn: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel = AdminViewModel.ViewModelFactory(
            ResourcesProvider(requireContext()),
            DatabaseDataSource()
        )
            .create(AdminViewModel::class.java)

        administratorsBtn = view.findViewById(R.id.admin_administrators_btn_cv)

        administratorsBtn.setOnClickListener {
            val administratorsDialog = AdministratorsDialog(mViewModel)

            administratorsDialog.show(
                requireActivity().supportFragmentManager,
                administratorsDialog.tag
            )
        }
    }
}