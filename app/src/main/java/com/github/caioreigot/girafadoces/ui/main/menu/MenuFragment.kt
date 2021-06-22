package com.github.caioreigot.girafadoces.ui.main.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.MenuItem
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.remote.DatabaseService
import com.github.caioreigot.girafadoces.data.remote.StorageService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private val mViewModel: MenuViewModel by viewModels()

    private lateinit var progressBar: ProgressBar
    private lateinit var menuRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        progressBar = view.findViewById(R.id.menu_fragment_progress_bar)
        menuRecyclerView = view.findViewById(R.id.menu_recycler_view)
        //endregion

        menuRecyclerView.adapter = MenuAdapter(listOf(), ResourcesProvider(requireContext()))

        menuRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false
        )

        menuRecyclerView.setHasFixedSize(true)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(menuRecyclerView)

        mViewModel.getMenuItems()

        //region Observers
        mViewModel.menuItemsLD.observe(viewLifecycleOwner, {
            it?.let { menuItems ->
                progressBar.visibility = View.GONE

                menuRecyclerView.adapter =
                    MenuAdapter(menuItems, ResourcesProvider(requireContext()))
            }
        })
        //endregion
    }
}