package com.github.caioreigot.girafadoces.ui.main.admin.administrators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.ui.main.admin.AdminPanelViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdministratorsDialog() : DialogFragment() {

    private val adminPanelViewModel: AdminPanelViewModel by viewModels()

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
            addAdminDialog = AddAdminDialog(adminPanelViewModel)

            addAdminDialog?.let {
                it.show(requireActivity().supportFragmentManager, it.tag)
            }
        }

        adminPanelViewModel.getAdministratorsUsers()

        //region Observers
        adminPanelViewModel.adminUsersItemsLD.observe(viewLifecycleOwner, {
            it?.let { adminUsersItems ->
                adminProgressBar.visibility = View.GONE

                adapter = AdministratorsAdapter(
                    adminPanelViewModel,
                    ResourcesProvider(requireContext()),
                    adminUsersItems.asReversed()
                )

                administratorsRecyclerView.adapter = adapter
            }
        })

        adminPanelViewModel.adminRemovedLD.observe(viewLifecycleOwner, {
            it?.let { position ->
                adapter.items.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, adapter.items.size)
            }
        })

        adminPanelViewModel.adminAddedLD.observe(viewLifecycleOwner, { adminAdded ->
            addAdminDialog?.dismiss()

            adapter.items.add(adminAdded)
            adapter.notifyItemInserted(adapter.items.size)
        })
        //endregion
    }

}