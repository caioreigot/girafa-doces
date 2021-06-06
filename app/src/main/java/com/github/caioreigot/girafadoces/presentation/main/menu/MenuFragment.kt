package com.github.caioreigot.girafadoces.presentation.main.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.remote.database.DatabaseDataSource
import com.github.caioreigot.girafadoces.data.remote.storage.StorageDataSource

class MenuFragment : Fragment() {

    lateinit var menuRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mViewModel: MenuViewModel = MenuViewModel.ViewModelFactory(
            ResourcesProvider(requireContext()),
            DatabaseDataSource(),
            StorageDataSource()
        )
            .create(MenuViewModel::class.java)

        menuRecyclerView = view.findViewById(R.id.menu_recycler_view)
        menuRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false
        )

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(menuRecyclerView)

        mViewModel.getMenuItems()

        //region Observers
        mViewModel.menuItemsLD.observe(viewLifecycleOwner, {
            it?.let { menuItems ->
                menuRecyclerView.adapter =
                    MenuAdapter(menuItems, ResourcesProvider(requireContext()))
            }
        })
        //endregion
    }
}