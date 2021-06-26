package com.github.caioreigot.girafadoces.ui.main.add

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
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
import java.util.ArrayList
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

    private var mListItems: MutableList<MenuItem>? = null
    private var mSavedListItems: MutableList<MenuItem>? = null

    private var mAddMenuItemDialog: DialogFragment? = null

    companion object {
        private const val RECYCLER_VIEW_SAVED_LIST_ID = "recycler_view_saved_list"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        progressBar = view.findViewById(R.id.add_fragment_progress_bar)
        addRecyclerView = view.findViewById(R.id.add_recycler_view)
        addItemBtn = view.findViewById(R.id.floating_btn_add_item)
        //endregion

        /* Passing an empty list to adapter
        while the asynchronous call to fetch
        items are not returned */
        addAdapter.setup(mutableListOf(), (activity as BottomNavActivity))
        addRecyclerView.adapter = addAdapter

        addRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(addRecyclerView)

        mSavedListItems = savedInstanceState
            ?.getParcelableArrayList(RECYCLER_VIEW_SAVED_LIST_ID)

        mSavedListItems?.let { savedListItems ->
            progressBar.visibility = View.GONE

            mListItems = savedListItems

            addAdapter.setup(savedListItems, (activity as BottomNavActivity))
            addRecyclerView.adapter = addAdapter
        }
            ?: addViewModel.getMenuItems()

        //region Listeners
        addItemBtn.setOnClickListener {
            mAddMenuItemDialog = AddMenuItemDialog(::clearRecyclerView)
            mAddMenuItemDialog?.let { dialog ->
                dialog.show(childFragmentManager, dialog.tag)
            }
        }
        //endregion

        //region Observers
        addViewModel.menuItemsLD.observe(viewLifecycleOwner, {
            it?.let { menuItems ->
                progressBar.visibility = View.GONE

                mListItems = menuItems

                addAdapter.setup(menuItems, (activity as BottomNavActivity))
                addRecyclerView.adapter = addAdapter
            }
        })

        addViewModel.errorMessageLD.observe(viewLifecycleOwner, { errorMessage ->
            val mainActivity = (activity as BottomNavActivity)

            mainActivity.showMessageDialog(
                MessageType.ERROR,
                R.string.dialog_error_title,
                errorMessage
            )
        })

        addViewModel.successMessageLD.observe(viewLifecycleOwner, { message ->
            (activity as BottomNavActivity).showMessageDialog(
                MessageType.SUCCESSFUL,
                R.string.dialog_successful_title,
                message,
                null
            )
        })
        //endregion
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mListItems?.let { listItems ->
            val parcelableArrayList: ArrayList<Parcelable> = ArrayList()

            for (i in listItems.indices)
                parcelableArrayList.add(listItems[i])

            outState.putParcelableArrayList(RECYCLER_VIEW_SAVED_LIST_ID, parcelableArrayList)
        }
    }

    private fun clearRecyclerView() {
        progressBar.visibility = View.VISIBLE
        addRecyclerView.adapter = null

        Log.d("MY_DEBUG", "clearRecyclerView() called!")
    }
}