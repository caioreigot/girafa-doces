package com.github.caioreigot.girafadoces.data.remote.auth

import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.Utils
import com.github.caioreigot.girafadoces.data.repository.FirebaseAuthRepository

class FirebaseAuthDataSource : FirebaseAuthRepository {

    override fun loginUser(
        email: String,
        password: String,
        loginCallback: (result: FirebaseResult) -> Unit
    ) {
        val (isValid, message) = Utils.infoVerification(
            email = email,
            password = password
        )

        if (!isValid) {
            loginCallback(FirebaseResult.Error(message))
            return
        }

        FirebaseAuthSingleton.mFirebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                when (task.isSuccessful) {
                    true -> loginCallback(FirebaseResult.Success("Login bem sucedido!"))
                    false -> loginCallback(FirebaseResult.Error("Erro ao logar"))
                }
            }
    }

    override fun registerUser(
        fullName: String,
        email: String,
        password: String,
        passwordConfirm: String,
        registerCallback: (result: FirebaseResult) -> Unit
    ) {
        val (isValid, message) = Utils.infoVerification(
            fullName = fullName,
            email = email,
            password = password,
            passwordConfirm = passwordConfirm
        )

        if (!isValid) {
            registerCallback(FirebaseResult.Error(message))
            return
        }

        FirebaseAuthSingleton.mFirebaseAuth
            .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                // TODO
            }
    }
}