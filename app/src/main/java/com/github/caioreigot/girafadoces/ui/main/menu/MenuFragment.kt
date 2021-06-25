package com.github.caioreigot.girafadoces.ui.main.menu

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : Fragment(R.layout.fragment_menu) {

    private val menuViewModel: MenuViewModel by viewModels()

    @Inject
    lateinit var menuAdapter: MenuAdapter

    private lateinit var progressBar: ProgressBar
    private lateinit var menuRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        progressBar = view.findViewById(R.id.menu_fragment_progress_bar)
        menuRecyclerView = view.findViewById(R.id.menu_recycler_view)
        //endregion

        /* Passing an empty list to adapter
        while the asynchronous call to return
        items is not called */
        menuAdapter.setup(listOf())
        menuRecyclerView.adapter = menuAdapter

        menuRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false
        )

        menuRecyclerView.setHasFixedSize(true)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(menuRecyclerView)

        menuViewModel.getMenuItems()

        //region Observers
        menuViewModel.menuItemsLD.observe(viewLifecycleOwner, {
            it?.let { menuItems ->
                progressBar.visibility = View.GONE

                menuAdapter.setup(menuItems)
                menuRecyclerView.adapter = menuAdapter
            }
        })
        //endregion
    }
}