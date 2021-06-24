package com.github.caioreigot.girafadoces.ui.main.add

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
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

@AndroidEntryPoint
class AddFragment : Fragment(R.layout.fragment_add) {

    private val mViewModel: AddViewModel by viewModels()

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

        addRecyclerView.adapter = AddAdapter(
            mutableListOf(),
            ResourcesProvider(requireContext()),
            (activity as MainActivity),
            DatabaseService(),
            StorageService()
        )

        addRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false)

        addRecyclerView.setHasFixedSize(true)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(addRecyclerView)

        mViewModel.getMenuItems()

        //region Listeners
        addItemBtn.setOnClickListener {
            activity?.supportFragmentManager?.let { supportFragmentManager ->
                val addMenuItemDialog = AddMenuItemDialog(::clearRecyclerView)
                addMenuItemDialog.show(supportFragmentManager, addMenuItemDialog.tag)
            }
        }
        //endregion

        //region Observers
        mViewModel.menuItemsLD.observe(viewLifecycleOwner, {
            it?.let { menuItems ->
                progressBar.visibility = View.GONE

                addRecyclerView.adapter = AddAdapter(
                    menuItems,
                    ResourcesProvider(requireContext()),
                    (activity as MainActivity),
                    DatabaseService(),
                    StorageService()
                )
            }
        })

        mViewModel.uploadProgressLD.observe(viewLifecycleOwner, {
            it?.let { percentageProgress ->
                // TODO
            }
        })

        mViewModel.errorMessageLD.observe(viewLifecycleOwner, { errorMessage ->
            val mainActivity = (activity as MainActivity)

            mainActivity.showMessageDialog(
                MessageType.ERROR,
                R.string.dialog_error_title,
                errorMessage
            )
        })

        mViewModel.successMessageLD.observe(viewLifecycleOwner, { message ->
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