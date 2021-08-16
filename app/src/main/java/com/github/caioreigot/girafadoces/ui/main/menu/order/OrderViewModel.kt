package com.github.caioreigot.girafadoces.ui.main.menu.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.caioreigot.girafadoces.data.helper.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.helper.Utils.Companion.parseUserToJson
import com.github.caioreigot.girafadoces.data.model.*
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val database: DatabaseRepository
) : ViewModel() {

    companion object {
        private const val VIEW_FLIPPER_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    private val _viewFlipper: MutableLiveData<Int> = MutableLiveData()
    val viewFlipper: LiveData<Int>
        get() = _viewFlipper

    private val _orderResult: SingleLiveEvent<ServiceResult> = SingleLiveEvent()
    val orderResult: LiveData<ServiceResult>
        get() = _orderResult

    private val minAmount: Int = 1
    private val maxAmount: Int = 999

    private var amountDefaultValue: Int = 1

    private val _amount = MutableLiveData<String>()
    val amount: LiveData<String>
        get() = _amount

    init {
        _amount.value = amountDefaultValue.toString()
    }

    fun increaseAmount() {
        if (amountDefaultValue >= maxAmount)
            return

        _amount.value = (++amountDefaultValue).toString()
    }

    fun decreaseAmount() {
        if (amountDefaultValue <= minAmount)
            return

        _amount.value = (--amountDefaultValue).toString()
    }

    fun confirmOrder(
        order: Order,
        userJson: String
    ) {
        _viewFlipper.value = VIEW_FLIPPER_PROGRESS_BAR

        database.sendUserOrder(order, userJson) { result ->
            _viewFlipper.value = VIEW_FLIPPER_BUTTON
            _orderResult.value = result
        }
    }
}