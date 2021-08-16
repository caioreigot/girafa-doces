package com.github.caioreigot.girafadoces.ui.main.admin.orders

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.Order

class OrdersAdapter(
    private val orders: MutableList<Order>,
    private val observationButtonClickListener: (String) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var timeOrderedTv: TextView
        private var orderedProductTv: TextView
        private var orderQuantityTv: TextView
        private var orderAddressTv: TextView
        private var observationButtonContainer: ViewGroup

        init {
            with (itemView) {
                timeOrderedTv = findViewById(R.id.time_ordered_tv)
                orderedProductTv = findViewById(R.id.ordered_product_tv)
                orderQuantityTv = findViewById(R.id.order_quantity_tv)
                orderAddressTv = findViewById(R.id.order_address_tv)
                observationButtonContainer = findViewById(R.id.observation_button_container)
            }
        }

        fun bind(order: Order) {
            timeOrderedTv.text = order.timeOrdered
            orderedProductTv.text = order.product.header
            orderQuantityTv.text = order.quantity.toString()
            orderAddressTv.text = order.user.deliveryAddress

            if (!TextUtils.isEmpty(order.userObservation)) {
                observationButtonContainer.setOnClickListener {
                    observationButtonClickListener(order.userObservation)
                }

                observationButtonContainer.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_order, parent, false)

        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size
}