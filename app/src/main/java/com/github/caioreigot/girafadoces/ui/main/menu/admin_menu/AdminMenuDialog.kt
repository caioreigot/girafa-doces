package com.github.caioreigot.girafadoces.ui.main.menu.admin_menu

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.MenuItem
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.ui.main.BottomNavActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminMenuDialog : DialogFragment(R.layout.fragment_add) {

    @Inject
    lateinit var adminMenuVMFactory: AdminMenuViewModel.Factory

    private val adminMenuViewModel: AdminMenuViewModel by viewModels(
        { this },
        { adminMenuVMFactory }
    )

    @Inject
    lateinit var adminMenuAdapter: AdminMenuAdapter

    lateinit var progressBar: ProgressBar

    lateinit var addRecyclerView: RecyclerView
    lateinit var addItemBtn: FloatingActionButton

    private var mListItems: MutableList<MenuItem>? = null
    private var mSavedListItems: MutableList<MenuItem>? = null

    private var mAddMenuItemDialog: DialogFragment? = null

    companion object {
        private const val RECYCLER_VIEW_SAVED_LIST_ID = "recycler_view_saved_list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        progressBar = view.findViewById(R.id.add_fragment_progress_bar)
        addRecyclerView = view.findViewById(R.id.add_recycler_view)
        addItemBtn = view.findViewById(R.id.floating_btn_add_item)
        //endregion

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        /* Passing an empty list to adapter
        while the asynchronous call to fetch
        items are not returned */
        adminMenuAdapter.setup(mutableListOf(), (activity as BottomNavActivity))
        addRecyclerView.adapter = adminMenuAdapter

        addRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(addRecyclerView)

        mSavedListItems = savedInstanceState
            ?.getParcelableArrayList(RECYCLER_VIEW_SAVED_LIST_ID)

        mSavedListItems?.let { savedListItems ->
            progressBar.visibility = View.GONE

            mListItems = savedListItems

            adminMenuAdapter.setup(savedListItems, (activity as BottomNavActivity))
            addRecyclerView.adapter = adminMenuAdapter
        }
            ?: adminMenuViewModel.getMenuItems()

        //region Listeners
        addItemBtn.setOnClickListener {
            mAddMenuItemDialog = AdminMenuItemDialog(::clearRecyclerView)
            mAddMenuItemDialog?.let { dialog ->
                dialog.show(childFragmentManager, dialog.tag)
            }
        }
        //endregion

        //region Observers
        adminMenuViewModel.menuItems.observe(viewLifecycleOwner, {
            it?.let { menuItems ->
                progressBar.visibility = View.GONE

                mListItems = menuItems

                adminMenuAdapter.setup(menuItems, (activity as BottomNavActivity))
                addRecyclerView.adapter = adminMenuAdapter
            }
        })

        adminMenuViewModel.errorMessage.observe(viewLifecycleOwner, { errorMessage ->
            val mainActivity = (activity as BottomNavActivity)

            mainActivity.showMessageDialog(
                MessageType.ERROR,
                R.string.dialog_error_title,
                errorMessage
            )
        })

        adminMenuViewModel.successMessage.observe(viewLifecycleOwner, { message ->
            (activity as BottomNavActivity).showMessageDialog(
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