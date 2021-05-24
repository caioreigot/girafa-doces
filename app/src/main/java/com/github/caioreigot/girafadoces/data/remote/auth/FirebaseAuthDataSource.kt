package com.github.caioreigot.girafadoces.data.remote.auth

import android.util.Log
import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.Singleton
import com.github.caioreigot.girafadoces.data.Utils
import com.github.caioreigot.girafadoces.data.model.Global
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

        Singleton.mFirebaseAuth.signInWithEmailAndPassword(email, password)
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
        deliveryAddress: String,
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

        Singleton.mFirebaseAuth
            .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                when (task.isSuccessful) {
                    true -> {
                        val userID = Singleton.mFirebaseAuth.currentUser?.uid

                        userID?.let { uid ->
                            val currentUserDB = Singleton.mDatabaseUsersReference.child(uid)

                            with (currentUserDB) {
                                child(Global.DatabaseNames.USER_FULL_NAME).setValue(fullName)
                                child(Global.DatabaseNames.USER_DELIVERY_ADDRESS).setValue(deliveryAddress)
                                child(Global.DatabaseNames.USER_EMAIL).setValue(email)
                            }
                        }

                        registerCallback(FirebaseResult.Success("Cadastro efetuado com sucesso!"))
                    }

                    false -> {
                        registerCallback(FirebaseResult.Success("Erro inesperado"))
                    }
                }
            }
    }
}