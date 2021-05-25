package com.github.caioreigot.girafadoces.data.remote.auth

import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.Singleton
import com.github.caioreigot.girafadoces.data.Utils
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.Global
import com.github.caioreigot.girafadoces.data.repository.FirebaseAuthRepository

class FirebaseAuthDataSource : FirebaseAuthRepository {

    override fun loginUser(
        email: String,
        password: String,
        callback: (result: FirebaseResult) -> Unit
    ) {
        val (isValid, errorType) = Utils.isLoginInformationValid(
            email = email,
            password = password
        )

        if (!isValid) {
            callback(FirebaseResult.Error(errorType!!))
            return
        }

        Singleton.mFirebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                when (task.isSuccessful) {
                    true -> callback(FirebaseResult.Success)

                    false -> callback(FirebaseResult.Error(ErrorType.NOT_FOUND))
                }
            }
    }

    override fun registerUser(
        fullName: String,
        email: String,
        deliveryAddress: String,
        postalNumber: String,
        password: String,
        passwordConfirm: String,
        callback: (result: FirebaseResult) -> Unit
    ) {
        val (isValid, errorType) = Utils.isRegisterInformationValid(
            fullName = fullName,
            email = email,
            deliveryAddress = deliveryAddress,
            postalNumber = postalNumber,
            password = password,
            passwordConfirm = passwordConfirm
        )

        if (!isValid) {
            callback(FirebaseResult.Error(errorType!!))
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

                        callback(FirebaseResult.Success)
                    }

                    false -> {
                        if (task.exception?.message == "The email address is already in use by another account.")
                            callback(FirebaseResult.Error(ErrorType.EMAIL_ALREADY_REGISTERED))
                        else
                            callback(FirebaseResult.Error(ErrorType.UNEXPECTED_ERROR))
                    }
                }
            }
    }

    override fun sendPasswordResetEmail(
        email: String,
        callback: (result: FirebaseResult) -> Unit
    ) {
        val (isValid, errorType) = Utils.isRegisterInformationValid(
            email = email
        )

        if (!isValid) {
            callback(FirebaseResult.Error(errorType!!))
            return
        }

        Singleton.mFirebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    callback(FirebaseResult.Success)
                else
                    callback(FirebaseResult.Error(ErrorType.NOT_FOUND))
            }
    }
}