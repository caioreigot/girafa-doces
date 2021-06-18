package com.github.caioreigot.girafadoces.presentation.main.admin.administrators

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.presentation.main.admin.AdminViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception

class AdministratorsDialog(private val adminViewModel: AdminViewModel) : DialogFragment() {

    lateinit var adminProgressBar: ProgressBar
    lateinit var administratorsRecyclerView: RecyclerView
    lateinit var adapter: AdministratorsAdapter

    lateinit var addAdminFloatingButton: FloatingActionButton
    var addAdminDialog: DialogFragment? = null

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.Theme_GirafaDoces)
    }
    */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.administrators_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        //region Assignments
        adminProgressBar = view.findViewById(R.id.admin_progress_bar)
        administratorsRecyclerView = view.findViewById(R.id.administrators_recycler_view)
        addAdminFloatingButton = view.findViewById(R.id.add_admin_floating_btn)
        //endregion

        administratorsRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.VERTICAL, false
        )

        addAdminFloatingButton.setOnClickListener {
            addAdminDialog = AddAdminDialog(adminViewModel)

            addAdminDialog?.let {
                it.show(requireActivity().supportFragmentManager, it.tag)
            }
        }

        adminViewModel.getAdministratorsUsers()

        //region Observers
        adminViewModel.adminUsersItemsLD.observe(viewLifecycleOwner, {
            it?.let { adminUsersItems ->
                adminProgressBar.visibility = View.GONE

                adapter = AdministratorsAdapter(
                    adminViewModel,
                    ResourcesProvider(requireContext()),
                    adminUsersItems.asReversed()
                )

                administratorsRecyclerView.adapter = adapter
            }
        })

        adminViewModel.adminRemovedLD.observe(viewLifecycleOwner, {
            it?.let { position ->
                adapter.items.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, adapter.items.size)
            }
        })

        adminViewModel.adminAddedLD.observe(viewLifecycleOwner, { adminAdded ->
            addAdminDialog?.dismiss()

            adapter.items.add(adminAdded)
            adapter.notifyItemInserted(adapter.items.size)
        })
        //endregion
    }

}