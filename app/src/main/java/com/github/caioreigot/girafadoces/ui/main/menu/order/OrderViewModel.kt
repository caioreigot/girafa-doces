package com.github.caioreigot.girafadoces.ui.main.menu.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val database: DatabaseRepository
) : ViewModel() {

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

    fun confirmOrder() {
        // TODO
    }
}