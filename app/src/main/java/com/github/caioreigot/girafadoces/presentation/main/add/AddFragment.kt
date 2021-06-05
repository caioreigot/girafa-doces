package com.github.caioreigot.girafadoces.presentation.main.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.remote.database.DatabaseDataSource
import com.github.caioreigot.girafadoces.data.remote.storage.StorageDataSource
import com.github.caioreigot.girafadoces.presentation.main.menu.MenuAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var addItemBtn: FloatingActionButton

    var dialogUploadPercentage: TextView? = null

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
        recyclerView = view.findViewById(R.id.add_recycler_view)
        addItemBtn = view.findViewById(R.id.floating_btn_add_item)
        //endregion

        recyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false)

        mViewModel.getMenuItems()

        //region Listeners
        addItemBtn.setOnClickListener {
            activity?.supportFragmentManager?.let { supportFragmentManager ->
                val addMenuItemDialog = AddMenuItemDialog(mViewModel)
                addMenuItemDialog.show(supportFragmentManager, addMenuItemDialog.tag)

                dialogUploadPercentage = addMenuItemDialog
                    .view?.findViewById(R.id.add_dialog_percentage_tv)
            }
        }
        //endregion

        //region Observers
        mViewModel.menuItemsLD.observe(viewLifecycleOwner, {
            // TODO: Desabilitar progress bar
            it?.let { menuItens ->
                recyclerView.adapter = MenuAdapter(menuItens)
            }
        })

        mViewModel.uploadProgressLD.observe(viewLifecycleOwner, {
            it?.let {  percentageProgress ->
                dialogUploadPercentage?.let { uploadPercentageTV ->
                    uploadPercentageTV.text = percentageProgress
                }
            }
        })
        //endregion
    }
}