package com.github.caioreigot.girafadoces.ui.main.admin.orders

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.Order

class OrdersAdapter(
    private val orders: MutableList<Order>,
    private val lightGrayColor: Int,
    private val observationButtonClickListener: (String) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var timeOrderedTv: TextView
        private var orderedProductTv: TextView
        private var orderQuantityTv: TextView
        private var orderAddressTv: TextView
        private var clientObservationButton: AppCompatImageButton

        init {
            with (itemView) {
                timeOrderedTv = findViewById(R.id.time_ordered_tv)
                orderedProductTv = findViewById(R.id.ordered_product_tv)
                orderQuantityTv = findViewById(R.id.order_quantity_tv)
                orderAddressTv = findViewById(R.id.order_address_tv)
                clientObservationButton = findViewById(R.id.client_observation_btn)
            }
        }

        fun bind(order: Order) {
            timeOrderedTv.text = order.timeOrdered
            orderedProductTv.text = order.product.header
            orderQuantityTv.text = order.quantity
            orderAddressTv.text = order.user.deliveryAddress

            if (!TextUtils.isEmpty(order.userObservation)) {
                clientObservationButton.setOnClickListener {
                    observationButtonClickListener(order.userObservation)
                }
            }
            else clientObservationButton.setColorFilter(lightGrayColor)
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