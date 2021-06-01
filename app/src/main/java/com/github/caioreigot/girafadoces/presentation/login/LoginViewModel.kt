package com.github.caioreigot.girafadoces.presentation.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.local.Preferences
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.User
import com.github.caioreigot.girafadoces.data.repository.FirebaseAuthRepository
import com.github.caioreigot.girafadoces.data.repository.FirebaseDatabaseRepository
import java.lang.IllegalArgumentException

class LoginViewModel(
    private val authDataSource: FirebaseAuthRepository,
    private val databaseDataSource: FirebaseDatabaseRepository,
    private val resProvider: ResourcesProvider,
    private val preferences: Preferences
) : ViewModel() {

    val loggedUserInformation: SingleLiveEvent<Pair<User?, String>> =
        SingleLiveEvent<Pair<User?, String>>()

    val loginBtnViewFlipper: MutableLiveData<Int> = MutableLiveData()
    val forgotPasswordBtnViewFlipper: MutableLiveData<Int> = MutableLiveData()

    val errorMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()

    val resetPasswordMessage: SingleLiveEvent<Pair<MessageType, String>> =
        SingleLiveEvent<Pair<MessageType, String>>()

    companion object {
        private const val VIEW_FLIPPER_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    fun loginUser(email: String, password: String) {
        // Show Progress Bar
        loginBtnViewFlipper.value = VIEW_FLIPPER_PROGRESS_BAR

        authDataSource.loginUser(email, password) { FirebaseAuthResult ->

            when (FirebaseAuthResult) {

                // If the user logs in, send the user information to view change activity
                is FirebaseResult.Success -> {
                    databaseDataSource.getLoggedUserInformation { User, FirebaseDBResult ->
                        when (FirebaseDBResult) {
                            is FirebaseResult.Success -> {
                                loggedUserInformation.value = Pair(User, password)
                            }

                            is FirebaseResult.Error -> {
                                loggedUserInformation.value = null

                                errorMessage.value = when (FirebaseDBResult.errorType) {
                                    ErrorType.SERVER_ERROR ->
                                        resProvider.getString(R.string.server_error_message)
                                    else ->
                                        resProvider.getString(R.string.unexpected_error)
                                }
                            }
                        }

                        loginBtnViewFlipper.value = VIEW_FLIPPER_BUTTON
                    }
                }

                is FirebaseResult.Error -> {
                    errorMessage.value = when (FirebaseAuthResult.errorType) {
                        ErrorType.UNEXPECTED_ERROR ->
                            resProvider.getString(R.string.unexpected_error)

                        // Information Validation
                        ErrorType.EMPTY_FIELD ->
                            resProvider.getString(R.string.empty_field)

                        ErrorType.INVALID_EMAIL ->
                            resProvider.getString(R.string.invalid_email_message)

                        ErrorType.WEAK_PASSWORD ->
                            resProvider.getString(R.string.weak_password_message)

                        else ->
                            resProvider.getString(R.string.login_error)
                    }

                    loginBtnViewFlipper.value = VIEW_FLIPPER_BUTTON
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        forgotPasswordBtnViewFlipper.value = VIEW_FLIPPER_PROGRESS_BAR

        authDataSource.sendPasswordResetEmail(email) { FirebaseAuthResult ->

            forgotPasswordBtnViewFlipper.value = VIEW_FLIPPER_BUTTON

            when (FirebaseAuthResult) {
                is FirebaseResult.Success ->
                    resetPasswordMessage.value = Pair(
                        MessageType.SUCCESSFUL, resProvider
                            .getString(R.string.reset_email_task_successful)
                    )

                is FirebaseResult.Error -> {
                    resetPasswordMessage.value = when (FirebaseAuthResult.errorType) {
                        ErrorType.INVALID_EMAIL -> Pair(MessageType.ERROR,
                            resProvider.getString(R.string.invalid_email_message))

                        else ->
                            Pair(MessageType.ERROR,
                                resProvider.getString(R.string.reset_email_task_error))
                    }
                }
            }
        }
    }

    fun rememberAccount(email: String, password: String) {
        preferences.setEmailAndPasswordValue(email, password)
    }

    fun searchRememberedAccount() {
        val (email, password) = preferences.getEmailAndPasswordValue()

        if (email != null && password != null)
            loginUser(email, password)
    }

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val authDataSource: FirebaseAuthRepository,
        private val databaseDataSource: FirebaseDatabaseRepository,
        private val resourceProvider: ResourcesProvider,
        private val preferences: Preferences
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java))
                return LoginViewModel(
                    authDataSource,
                    databaseDataSource,
                    resourceProvider,
                    preferences
                ) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}