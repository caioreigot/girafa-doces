package com.github.caioreigot.girafadoces.presentation.main.add

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.remote.database.DatabaseDataSource
import com.github.caioreigot.girafadoces.data.remote.storage.StorageDataSource
import com.github.caioreigot.girafadoces.data.repository.StorageRepository
import com.github.caioreigot.girafadoces.presentation.main.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddFragment : Fragment() {

    lateinit var progressBar: ProgressBar

    lateinit var addRecyclerView: RecyclerView
    lateinit var addItemBtn: FloatingActionButton

    var dialogUploadPercentage: TextView? = null
    var mDataset: ArrayList<out Parcelable>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mViewModel: AddViewModel = AddViewModel.ViewModelFactory(
            ResourcesProvider(requireContext()),
            StorageDataSource(),
            DatabaseDataSource()
        )
            .create(AddViewModel::class.java)

        //region Assignments
        progressBar = view.findViewById(R.id.add_fragment_progress_bar)
        addRecyclerView = view.findViewById(R.id.add_recycler_view)
        addItemBtn = view.findViewById(R.id.floating_btn_add_item)
        //endregion

        addRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false)

        addRecyclerView.setHasFixedSize(true)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(addRecyclerView)

        mViewModel.getMenuItems()

        //region Listeners
        addItemBtn.setOnClickListener {
            activity?.supportFragmentManager?.let { supportFragmentManager ->
                val addMenuItemDialog = AddMenuItemDialog(mViewModel, ::clearRecyclerView)
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
                    DatabaseDataSource(),
                    StorageDataSource()
                )
            }
        })

        mViewModel.uploadProgressLD.observe(viewLifecycleOwner, {
            it?.let { percentageProgress ->
                // TODO: Notificação com o progresso
            }
        })

        mViewModel.errorMessageLD.observe(viewLifecycleOwner, { errorMessage ->
            (activity as MainActivity).showMessageDialog(
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