package com.github.caioreigot.girafadoces.data.repository

import com.github.caioreigot.girafadoces.data.model.ServiceResult

interface AuthRepository {
    fun loginUser(
        email: String,
        password: String,
        callback: (serviceResult: ServiceResult) -> Unit
    )

    fun registerUser(
        fullName: String,
        email: String,
        phoneNumber: String,
        deliveryAddress: String,
        postalNumber: String,
        password: String,
        passwordConfirm: String,
        callback: (serviceResult: ServiceResult) -> Unit
    )

    fun sendPasswordResetEmail(
        email: String,
        callback: (serviceResult: ServiceResult) -> Unit
    )
}