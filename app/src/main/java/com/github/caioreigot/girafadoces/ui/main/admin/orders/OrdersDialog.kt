package com.github.caioreigot.girafadoces.ui.main.admin.orders

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersDialog : DialogFragment(R.layout.dialog_orders) {

    private val ordersViewModel: OrdersViewModel by viewModels()

    lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (view) {
            recyclerView = findViewById(R.id.orders_recycler_view)
        }

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        ordersViewModel.getOrders()

        ordersViewModel.orders.observe(viewLifecycleOwner, {
            it?.let { orders ->
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = OrdersAdapter(
                    orders,
                    ContextCompat.getColor(requireContext(), R.color.lightGray),
                    ::observationButtonClickListener
                )
            }
        })
    }

    private fun observationButtonClickListener(observation: String) {
        // TODO
    }
}