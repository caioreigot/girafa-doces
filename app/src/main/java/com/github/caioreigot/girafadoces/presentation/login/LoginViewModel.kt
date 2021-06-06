package com.github.caioreigot.girafadoces.presentation.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.FirebaseResult
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.local.Preferences
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.User
import com.github.caioreigot.girafadoces.data.repository.AuthRepository
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import java.lang.IllegalArgumentException

class LoginViewModel(
    private val authDataSource: AuthRepository,
    private val databaseDataSource: DatabaseRepository,
    private val resProvider: ResourcesProvider,
    private val preferences: Preferences
) : ViewModel() {

    val loggedUserInformationLD: SingleLiveEvent<Pair<User?, String>> =
        SingleLiveEvent<Pair<User?, String>>()

    val loginBtnViewFlipperLD: MutableLiveData<Int> = MutableLiveData()
    val forgotPasswordBtnViewFlipperLD: MutableLiveData<Int> = MutableLiveData()

    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()

    val resetPasswordMessageLD: SingleLiveEvent<Pair<MessageType, String>> =
        SingleLiveEvent<Pair<MessageType, String>>()

    companion object {
        private const val VIEW_FLIPPER_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    fun loginUser(email: String, password: String) {
        // Show Progress Bar
        loginBtnViewFlipperLD.value = VIEW_FLIPPER_PROGRESS_BAR

        authDataSource.loginUser(email, password) { FirebaseAuthResult ->

            when (FirebaseAuthResult) {

                // If the user logs in, send the user information to view change activity
                is FirebaseResult.Success -> {
                    databaseDataSource.getLoggedUserInformation { User, FirebaseDBResult ->
                        when (FirebaseDBResult) {
                            is FirebaseResult.Success -> {
                                loggedUserInformationLD.value = Pair(User, password)
                            }

                            is FirebaseResult.Error -> {
                                loggedUserInformationLD.value = null

                                errorMessageLD.value = when (FirebaseDBResult.errorType) {
                                    ErrorType.SERVER_ERROR ->
                                        resProvider.getString(R.string.server_error_message)
                                    else ->
                                        resProvider.getString(R.string.unexpected_error)
                                }
                            }
                        }

                        loginBtnViewFlipperLD.value = VIEW_FLIPPER_BUTTON
                    }
                }

                is FirebaseResult.Error -> {
                    errorMessageLD.value = when (FirebaseAuthResult.errorType) {
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

                    loginBtnViewFlipperLD.value = VIEW_FLIPPER_BUTTON
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        forgotPasswordBtnViewFlipperLD.value = VIEW_FLIPPER_PROGRESS_BAR

        authDataSource.sendPasswordResetEmail(email) { FirebaseAuthResult ->

            forgotPasswordBtnViewFlipperLD.value = VIEW_FLIPPER_BUTTON

            when (FirebaseAuthResult) {
                is FirebaseResult.Success ->
                    resetPasswordMessageLD.value = Pair(
                        MessageType.SUCCESSFUL, resProvider
                            .getString(R.string.reset_email_task_successful)
                    )

                is FirebaseResult.Error -> {
                    resetPasswordMessageLD.value = when (FirebaseAuthResult.errorType) {
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
        private val authDataSource: AuthRepository,
        private val databaseDataSource: DatabaseRepository,
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