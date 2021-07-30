package com.github.caioreigot.girafadoces.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val database: DatabaseRepository,
    private val resProvider: ResourcesProvider,
    private val preferences: Preferences
) : ViewModel() {

    companion object {
        private const val VIEW_FLIPPER_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    private val _loggedUserInformationLD: SingleLiveEvent<Pair<User?, String>> =
        SingleLiveEvent<Pair<User?, String>>()
    val loggedUserInformationLD: LiveData<Pair<User?, String>>
        get() = _loggedUserInformationLD

    private val _loginBtnViewFlipperLD: MutableLiveData<Int> = MutableLiveData()
    val loginBtnViewFlipperLD: LiveData<Int>
        get() = _loginBtnViewFlipperLD

    private val _forgotPasswordBtnViewFlipperLD: MutableLiveData<Int> = MutableLiveData()
    val forgotPasswordBtnViewFlipperLD: LiveData<Int>
        get() = _forgotPasswordBtnViewFlipperLD

    private val _errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val errorMessageLD: LiveData<String>
        get() = _errorMessageLD

    private val _resetPasswordMessageLD: SingleLiveEvent<Pair<MessageType, String>> =
        SingleLiveEvent<Pair<MessageType, String>>()
    val resetPasswordMessageLD: LiveData<Pair<MessageType, String>>
        get() = _resetPasswordMessageLD

    fun loginUser(email: String, password: String) {
        // Show Progress Bar
        _loginBtnViewFlipperLD.value = VIEW_FLIPPER_PROGRESS_BAR

        viewModelScope.launch {
            auth.loginUser(email, password) { authResult ->
                when (authResult) {
                    // If the user logs in, send the user information to view change activity
                    is ServiceResult.Success -> {
                        database.getLoggedUserInformation { user, databaseResult ->
                            when (databaseResult) {
                                is ServiceResult.Success ->
                                    _loggedUserInformationLD.value = Pair(user, password)

                                is ServiceResult.Error ->
                                    _errorMessageLD.value =
                                        ErrorMessageHandler
                                            .getErrorMessage(resProvider, databaseResult.errorType)
                            }

                            _loginBtnViewFlipperLD.value = VIEW_FLIPPER_BUTTON
                        }
                    }

                    is ServiceResult.Error -> {
                        _errorMessageLD.value = ErrorMessageHandler
                            .getErrorMessage(resProvider, authResult.errorType)

                        _loginBtnViewFlipperLD.value = VIEW_FLIPPER_BUTTON
                    }
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        _forgotPasswordBtnViewFlipperLD.value = VIEW_FLIPPER_PROGRESS_BAR

        auth.sendPasswordResetEmail(email) { authResult ->
            _forgotPasswordBtnViewFlipperLD.value = VIEW_FLIPPER_BUTTON

            when (authResult) {
                is ServiceResult.Success ->
                    _resetPasswordMessageLD.value = Pair(
                        MessageType.SUCCESSFUL,
                        resProvider.getString(R.string.reset_email_task_successful)
                    )

                is ServiceResult.Error -> {
                    _resetPasswordMessageLD.value = when (authResult.errorType) {
                        ErrorType.INVALID_EMAIL -> Pair(
                            MessageType.ERROR,
                            resProvider.getString(R.string.invalid_email_message)
                        )

                        else ->
                            Pair(
                                MessageType.ERROR,
                                resProvider.getString(R.string.reset_email_task_error)
                            )
                    }
                }
            }
        }
    }

    fun rememberAccount(email: String, password: String) =
        preferences.setEmailAndPasswordValue(email, password)

    fun searchRememberedAccount() {
        val (email, password) = preferences.getEmailAndPasswordValue()

        if (email != null && password != null)
            loginUser(email, password)
    }
}