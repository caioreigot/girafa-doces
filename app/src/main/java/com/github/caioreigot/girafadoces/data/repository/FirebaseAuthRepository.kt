package com.github.caioreigot.girafadoces.data.repository

import com.github.caioreigot.girafadoces.data.FirebaseResult

interface FirebaseAuthRepository {
    fun loginUser(
        email: String,
        password: String,
        callback: (result: FirebaseResult) -> Unit
    )

    fun registerUser(
        fullName: String,
        email: String,
        phoneDDD: String,
        phoneNumber: String,
        deliveryAddress: String,
        postalNumber: String,
        password: String,
        passwordConfirm: String,
        callback: (result: FirebaseResult) -> Unit
    )

    fun sendPasswordResetEmail(
        email: String,
        callback: (result: FirebaseResult) -> Unit
    )
}