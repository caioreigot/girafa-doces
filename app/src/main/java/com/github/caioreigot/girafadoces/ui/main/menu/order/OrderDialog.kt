package com.github.caioreigot.girafadoces.ui.main.menu.order

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.github.caioreigot.girafadoces.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDialog : DialogFragment(R.layout.order_dialog) {

    private val orderViewModel: OrderViewModel by viewModels()

    private lateinit var amountTV: TextView
    private lateinit var leftArrowBtnIV: ImageView
    private lateinit var rightArrowBtnIV: ImageView
    private lateinit var confirmOrderBtnCV: CardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        amountTV = view.findViewById(R.id.order_dialog_amount_tv)
        leftArrowBtnIV = view.findViewById(R.id.order_dialog_left_arrow_iv)
        rightArrowBtnIV = view.findViewById(R.id.order_dialog_right_arrow_iv)
        confirmOrderBtnCV = view.findViewById(R.id.confirm_order_btn_cv)
        //endregion

        //region Listeners
        leftArrowBtnIV.setOnClickListener {
            orderViewModel.decreaseAmount()
        }

        rightArrowBtnIV.setOnClickListener {
            orderViewModel.increaseAmount()
        }

        confirmOrderBtnCV.setOnClickListener {
            orderViewModel.confirmOrder()
        }
        //endregion

        //region Observers
        orderViewModel.amount.observe(viewLifecycleOwner, {
            it?.let { amount ->
                amountTV.text = amount
            }
        })
        //endregion
    }
}