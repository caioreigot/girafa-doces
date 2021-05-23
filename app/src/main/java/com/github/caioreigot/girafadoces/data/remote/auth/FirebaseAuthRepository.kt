package com.github.caioreigot.girafadoces.data.remote.auth

interface FirebaseAuthRepository {
    fun loginUser(
        email: String,
        password: String,
        callback: (loginStatus: Boolean, errorMessage: String?) -> Unit
    )
}