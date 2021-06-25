package com.github.caioreigot.girafadoces.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.helper.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.local.Preferences
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.User
import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.repository.AuthRepository
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val database: DatabaseRepository,
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

        auth.loginUser(email, password) { authResult ->

            when (authResult) {

                // If the user logs in, send the user information to view change activity
                is ServiceResult.Success -> {
                    database.getLoggedUserInformation { user, databaseResult ->
                        when (databaseResult) {
                            is ServiceResult.Success -> {
                                loggedUserInformationLD.value = Pair(user, password)
                            }

                            is ServiceResult.Error -> {
                                loggedUserInformationLD.value = null

                                errorMessageLD.value =
                                    ErrorMessageHandler
                                        .getErrorMessage(resProvider, databaseResult.errorType)
                            }
                        }

                        loginBtnViewFlipperLD.value = VIEW_FLIPPER_BUTTON
                    }
                }

                is ServiceResult.Error -> {
                    errorMessageLD.value = ErrorMessageHandler
                        .getErrorMessage(resProvider, authResult.errorType)

                    loginBtnViewFlipperLD.value = VIEW_FLIPPER_BUTTON
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        forgotPasswordBtnViewFlipperLD.value = VIEW_FLIPPER_PROGRESS_BAR

        auth.sendPasswordResetEmail(email) { authResult ->

            forgotPasswordBtnViewFlipperLD.value = VIEW_FLIPPER_BUTTON

            when (authResult) {
                is ServiceResult.Success ->
                    resetPasswordMessageLD.value = Pair(
                        MessageType.SUCCESSFUL, resProvider
                            .getString(R.string.reset_email_task_successful)
                    )

                is ServiceResult.Error -> {
                    resetPasswordMessageLD.value = when (authResult.errorType) {
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
}