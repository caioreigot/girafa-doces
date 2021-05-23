package com.github.caioreigot.girafadoces.data.repository

import com.github.caioreigot.girafadoces.data.FirebaseResult

interface FirebaseAuthRepository {
    fun loginUser(
        email: String,
        password: String,
        loginCallback: (result: FirebaseResult) -> Unit
    )

    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        passwordConfirm: String,
        registerCallback: (result: FirebaseResult) -> Unit
    )
}