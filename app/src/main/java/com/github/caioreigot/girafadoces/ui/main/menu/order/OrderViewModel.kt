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

    private val MIN_AMOUNT: Int = 1
    private val MAX_AMOUNT: Int = 999

    private var amountValue: Int = 1

    private val _amount = MutableLiveData<String>()
    val amount: LiveData<String>
        get() = _amount

    init {
        _amount.value = amountValue.toString()
    }

    fun increaseAmount() {
        if (amountValue >= MAX_AMOUNT)
            return

        _amount.value = (++amountValue).toString()
    }

    fun decreaseAmount() {
        if (amountValue <= MIN_AMOUNT)
            return

        _amount.value = (--amountValue).toString()
    }

    fun confirmOrder() {
        // TODO
    }

}