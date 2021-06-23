package com.github.caioreigot.girafadoces.ui.main.admin.administrators

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.ui.main.admin.AdminPanelViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdministratorsDialog : DialogFragment() {

    @Inject lateinit var adminPanelVMFactory: AdminPanelViewModel.ViewModelFactory

    private val adminPanelViewModel: AdminPanelViewModel by viewModels(
        { this },
        { adminPanelVMFactory }
    )

    /*private val adminPanelViewModel: AdminPanelViewModel by lazy {
        ViewModelProvider(this, adminPanelVMFactory)
            .get(AdminPanelViewModel::class.java)
    }*/

    private lateinit var adminProgressBar: ProgressBar
    private lateinit var administratorsRecyclerView: RecyclerView
    private lateinit var adapter: AdministratorsAdapter

    private lateinit var addAdminFloatingButton: FloatingActionButton
    private var addAdminDialog: DialogFragment? = null

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.Theme_GirafaDoces)
    }*/

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
            addAdminDialog = AddAdminDialog()

            addAdminDialog?.let { itDialogFragment ->
                itDialogFragment.show(
                    childFragmentManager,
                    itDialogFragment.tag
                )
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