package com.github.caioreigot.girafadoces.data.repository

import android.app.Service
import com.github.caioreigot.girafadoces.data.model.*

interface DatabaseRepository {
    fun getLoggedUserInformation(
        callback: (User?, serviceResult: ServiceResult) -> Unit
    )

    fun getUserByUid(
        uid: String,
        callback: (User?, serviceResult: ServiceResult) -> Unit
    )

    fun getAdministratorsUsers(
        callback: (MutableList<User>?, serviceResult: ServiceResult) -> Unit
    )

    fun getAdministratorUidByEmail(
        email: String,
        callback: (uid: String?, serviceResult: ServiceResult) -> Unit
    )

    fun changeAccountField(
        accountField: UserAccountField,
        newValue: Any,
        callback: (serviceResult: ServiceResult) -> Unit
    )

    fun getMenuItems(
        storage: StorageRepository,
        callback: (MutableList<MenuItem>?, serviceResult: ServiceResult) -> Unit
    )

    fun saveMenuItem(
        itemHeader: String,
        itemContent: String,
        callback: (uid: String?, serviceResult: ServiceResult) -> Unit
    )

    fun sendUserOrder(
        order: Order,
        userJson: String,
        callback: (ServiceResult) -> Unit
    )

    fun getAllUsersOrders(
        callback: (orders: MutableList<Order>?, result: ServiceResult) -> Unit
    )

    fun removeMenuItem(
        storage: StorageRepository,
        uid: String,
        callback: (serviceResult: ServiceResult) -> Unit
    )
}