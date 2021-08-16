package com.github.caioreigot.girafadoces.ui.main.admin.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.caioreigot.girafadoces.data.model.Order
import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val database: DatabaseRepository
) : ViewModel() {

    private val _orders: MutableLiveData<MutableList<Order>> = MutableLiveData()
    val orders: LiveData<MutableList<Order>>
        get() = _orders

    fun getOrders() {
        database.getAllUsersOrders { orders, result ->
            when (result) {
                is ServiceResult.Success -> _orders.value = orders
                is ServiceResult.Error -> {/*TODO*/}
            }
        }
    }
}