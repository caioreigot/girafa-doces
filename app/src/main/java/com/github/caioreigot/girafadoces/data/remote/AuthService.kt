package com.github.caioreigot.girafadoces.data.remote

import com.github.caioreigot.girafadoces.data.helper.Utils
import com.github.caioreigot.girafadoces.data.model.*
import com.github.caioreigot.girafadoces.data.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.tasks.await

class AuthService : AuthRepository {

    override suspend fun loginUser(
        email: String,
        password: String,
        callback: (serviceResult: ServiceResult) -> Unit
    ) {
        val (isValid, errorType) = Utils.isLoginInformationValid(
            email = email,
            password = password
        )

        if (!isValid) {
            callback(ServiceResult.Error(errorType!!))
            return
        }

        withTimeout(Global.AUTH_TIME_OUT_IN_MILLIS) {
            try {
                Singleton.AUTH
                    .signInWithEmailAndPassword(email, password)
                    .await()

                callback(ServiceResult.Success)
            } catch (e: Exception) {
                when (e) {
                    is TimeoutCancellationException ->
                        callback(ServiceResult.Error(ErrorType.LOGIN_TIME_OUT))

                    is FirebaseNetworkException ->
                        callback(ServiceResult.Error(ErrorType.NETWORK_EXCEPTION))

                    is FirebaseAuthInvalidUserException ->
                        callback(ServiceResult.Error(ErrorType.AUTH_INVALID_USER))

                    is FirebaseAuthInvalidCredentialsException ->
                        callback(ServiceResult.Error(ErrorType.AUTH_INVALID_USER))

                    else ->
                        callback(ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
                }
            }
        }
    }

    override fun registerUser(
        fullName: String,
        email: String,
        phoneNumber: String,
        deliveryAddress: String,
        postalNumber: String,
        password: String,
        passwordConfirm: String,
        callback: (serviceResult: ServiceResult) -> Unit
    ) {
        val (isValid, errorType) = Utils.isRegisterInformationValid(
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            deliveryAddress = deliveryAddress,
            postalNumber = postalNumber,
            password = password,
            passwordConfirm = passwordConfirm
        )

        if (!isValid) {
            callback(ServiceResult.Error(errorType!!))
            return
        }

        // Adding postal number with the address
        val fullDeliveryAddress = "$deliveryAddress - nº $postalNumber"

        Singleton.AUTH
            .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                when (task.isSuccessful) {
                    true -> {
                        val userID = Singleton.AUTH.currentUser?.uid

                        userID?.let { uid ->
                            val currentUserDB = Singleton.DATABASE_USERS_REF.child(uid)

                            with (currentUserDB) {
                                child(Global.DatabaseNames.USER_FULL_NAME).setValue(fullName)
                                child(Global.DatabaseNames.USER_DELIVERY_ADDRESS).setValue(fullDeliveryAddress)
                                child(Global.DatabaseNames.USER_EMAIL).setValue(email)
                                child(Global.DatabaseNames.USER_PHONE).setValue(phoneNumber)
                            }
                        }

                        callback(ServiceResult.Success)
                    }

                    false -> {
                        if (task.exception?.message == "The email address is already in use by another account.")
                            callback(ServiceResult.Error(ErrorType.EMAIL_ALREADY_REGISTERED))
                        else
                            callback(ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
                    }
                }
            }
    }

    override fun sendPasswordResetEmail(
        email: String,
        callback: (serviceResult: ServiceResult) -> Unit
    ) {
        if (!Utils.isValidEmail(email)) {
            callback(ServiceResult.Error(ErrorType.INVALID_EMAIL))
            return
        }

        Singleton.AUTH.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    callback(ServiceResult.Success)
                else
                    callback(ServiceResult.Error(ErrorType.ACCOUNT_NOT_FOUND))
            }
    }
}