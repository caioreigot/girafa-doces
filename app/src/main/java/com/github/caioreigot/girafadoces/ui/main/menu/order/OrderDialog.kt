package com.github.caioreigot.girafadoces.ui.main.menu.order

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.Utils
import com.github.caioreigot.girafadoces.data.model.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class OrderDialog(
    private val product: Product,
    private val handleOrderCallback: (ServiceResult) -> Unit
) : DialogFragment(R.layout.dialog_order) {

    private val orderViewModel: OrderViewModel by viewModels()

    private lateinit var amountTV: TextView
    private lateinit var leftArrowBtnIV: ImageView
    private lateinit var rightArrowBtnIV: ImageView
    private lateinit var userObservationET: EditText
    private lateinit var confirmOrderBtnCV: CardView
    private lateinit var viewFlipper: ViewFlipper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        with (view) {
            amountTV = findViewById(R.id.order_dialog_amount_tv)
            leftArrowBtnIV = findViewById(R.id.order_dialog_left_arrow_iv)
            rightArrowBtnIV = findViewById(R.id.order_dialog_right_arrow_iv)
            userObservationET = findViewById(R.id.observation_et)
            confirmOrderBtnCV = findViewById(R.id.confirm_order_btn_cv)
            viewFlipper = findViewById(R.id.confirm_order_view_flipper)
        }
        //endregion

        //region Listeners
        leftArrowBtnIV.setOnClickListener {
            orderViewModel.decreaseAmount()
        }

        rightArrowBtnIV.setOnClickListener {
            orderViewModel.increaseAmount()
        }

        confirmOrderBtnCV.setOnClickListener {
            dialog?.setCancelable(false)
            dialog?.setCanceledOnTouchOutside(false)

            val userJson = Utils.parseUserToJson(UserSingleton.getUserObject())
            val userUid = Singleton.AUTH.currentUser?.uid ?: ""

            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ROOT)
            val date = formatter.format(Date())
            val dateFormatted = date.replace(" ", " as ")

            val order = Order(
                userUid = userUid,
                user = user {},
                product = product,
                timeOrdered = dateFormatted,
                quantity = amountTV.text.toString(),
                userObservation = userObservationET.text.toString(),
            )

            orderViewModel.confirmOrder(order, userJson)
        }
        //endregion

        //region Observers
        with (orderViewModel) {
            val thisActivity = this@OrderDialog

            amount.observe(viewLifecycleOwner, {
                it?.let { amount ->
                    amountTV.text = amount
                }
            })

            viewFlipper.observe(viewLifecycleOwner, {
                it?.let { childToDisplay ->
                    thisActivity.viewFlipper.displayedChild = childToDisplay
                }
            })

            orderResult.observe(viewLifecycleOwner, {
                it?.let { result ->
                    handleOrderCallback(result)
                    dialog?.dismiss()
                }
            })
        }
        //endregion
    }
}