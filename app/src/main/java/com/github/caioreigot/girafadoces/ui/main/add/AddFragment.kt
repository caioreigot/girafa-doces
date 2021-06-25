package com.github.caioreigot.girafadoces.ui.main.add

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
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.remote.DatabaseService
import com.github.caioreigot.girafadoces.data.remote.StorageService
import com.github.caioreigot.girafadoces.ui.main.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddFragment : Fragment(R.layout.fragment_add) {

    @Inject
    lateinit var addVMFactory: AddViewModel.Factory

    private val addViewModel: AddViewModel by viewModels(
        { this },
        { addVMFactory }
    )

    @Inject
    lateinit var addAdapter: AddAdapter

    lateinit var progressBar: ProgressBar

    lateinit var addRecyclerView: RecyclerView
    lateinit var addItemBtn: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        progressBar = view.findViewById(R.id.add_fragment_progress_bar)
        addRecyclerView = view.findViewById(R.id.add_recycler_view)
        addItemBtn = view.findViewById(R.id.floating_btn_add_item)
        //endregion

        /* Passing an empty list to adapter
        while the asynchronous call to return
        items is not called */
        addAdapter.setup(mutableListOf(), (activity as MainActivity))
        addRecyclerView.adapter = addAdapter

        addRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false)

        addRecyclerView.setHasFixedSize(true)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(addRecyclerView)

        addViewModel.getMenuItems()

        //region Listeners
        addItemBtn.setOnClickListener {
            val addMenuItemDialog = AddMenuItemDialog(::clearRecyclerView)
            addMenuItemDialog.show(childFragmentManager, addMenuItemDialog.tag)
        }
        //endregion

        //region Observers
        addViewModel.menuItemsLD.observe(viewLifecycleOwner, {
            it?.let { menuItems ->
                progressBar.visibility = View.GONE

                addAdapter.setup(menuItems, (activity as MainActivity))
                addRecyclerView.adapter = addAdapter
            }
        })

        addViewModel.errorMessageLD.observe(viewLifecycleOwner, { errorMessage ->
            val mainActivity = (activity as MainActivity)

            mainActivity.showMessageDialog(
                MessageType.ERROR,
                R.string.dialog_error_title,
                errorMessage
            )
        })

        addViewModel.successMessageLD.observe(viewLifecycleOwner, { message ->
            (activity as MainActivity).showMessageDialog(
                MessageType.SUCCESSFUL,
                R.string.dialog_successful_title,
                message,
                null
            )
        })
        //endregion
    }

    private fun clearRecyclerView() {
        progressBar.visibility = View.VISIBLE
        addRecyclerView.adapter = null
    }
}