package com.github.caioreigot.girafadoces.ui.main.menu

import android.app.Dialog
import android.app.Service
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.Product
import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.model.UserSingleton
import com.github.caioreigot.girafadoces.ui.main.BottomNavActivity
import com.github.caioreigot.girafadoces.ui.main.menu.admin_menu.AdminMenuDialog
import com.github.caioreigot.girafadoces.ui.main.menu.order.OrderDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : Fragment(R.layout.fragment_menu) {

    private val menuViewModel: MenuViewModel by viewModels()

    @Inject
    lateinit var menuAdapter: MenuAdapter

    private lateinit var progressBar: ProgressBar
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var menuAdminFab: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        progressBar = view.findViewById(R.id.menu_fragment_progress_bar)
        menuRecyclerView = view.findViewById(R.id.menu_recycler_view)
        menuAdminFab = view.findViewById(R.id.floating_btn_admin_menu)
        //endregion

        /* Passing an empty list to adapter
        while the asynchronous call to fetch
        items are not returned */
        menuAdapter.setup(listOf(), ::openOrderDialog)
        menuRecyclerView.adapter = menuAdapter

        menuRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false
        )

        menuRecyclerView.setHasFixedSize(true)
        
        if (UserSingleton.isAdmin) {
            menuAdminFab.visibility = View.VISIBLE

            menuAdminFab.setOnClickListener {
                val adminMenuDialog = AdminMenuDialog()
                adminMenuDialog.show(childFragmentManager, adminMenuDialog.tag)
            }
        }

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(menuRecyclerView)

        menuViewModel.getMenuItems()

        //region Observers
        menuViewModel.menuItems.observe(viewLifecycleOwner, {
            it?.let { menuItems ->
                progressBar.visibility = View.GONE

                menuAdapter.setup(menuItems, ::openOrderDialog)
                menuRecyclerView.adapter = menuAdapter
            }
        })
        //endregion
    }

    private fun openOrderDialog(product: Product) {
        val orderDialog = OrderDialog(product, ::handleOrderResult)
        orderDialog.show(childFragmentManager, orderDialog.tag)
    }

    private fun handleOrderResult(result: ServiceResult) {
        when (result) {
            is ServiceResult.Success -> {
                val dialog = Dialog(requireContext())

                dialog.apply {
                    setContentView(R.layout.dialog_order_confirmed)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }

                val check: ImageView = dialog.findViewById(R.id.check)
                val drawable = check.drawable
                val avd = drawable as AnimatedVectorDrawableCompat

                dialog.findViewById<ImageView>(R.id.close_button_iv).setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

                Handler(Looper.getMainLooper()).postDelayed({
                    avd.start()
                }, 300)

                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 4000)
            }

            is ServiceResult.Error -> {
                val message = ErrorMessageHandler.getErrorMessage(
                    ResourcesProvider(requireContext()),
                    result.errorType
                )

                (activity as BottomNavActivity).showMessageDialog(
                    MessageType.ERROR,
                    R.string.dialog_error_title,
                    message
                )
            }
        }
    }
}