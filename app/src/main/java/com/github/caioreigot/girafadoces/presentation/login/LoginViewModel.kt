package com.github.caioreigot.girafadoces.presentation.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.repository.FirebaseAuthRepository
import java.lang.IllegalArgumentException

class LoginViewModel(
    private val dataSource: FirebaseAuthRepository,
    private val resProvider: ResourcesProvider
) : ViewModel() {

    val loggedIn: SingleLiveEvent<Boolean> = SingleLiveEvent<Boolean>()

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

        dataSource.loginUser(email, password) { FirebaseResult ->

            loginBtnViewFlipper.value = VIEW_FLIPPER_BUTTON

            when (FirebaseResult) {
                is FirebaseResult.Success -> loggedIn.value = true

                is FirebaseResult.Error -> {
                    errorMessage.value = when (FirebaseResult.errorType) {
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
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        forgotPasswordBtnViewFlipper.value = VIEW_FLIPPER_PROGRESS_BAR

        dataSource.sendPasswordResetEmail(email) { FirebaseResult ->

            forgotPasswordBtnViewFlipper.value = VIEW_FLIPPER_BUTTON

            when (FirebaseResult) {
                is FirebaseResult.Success ->
                    resetPasswordMessage.value = Pair(
                        MessageType.SUCCESSFUL, resProvider
                            .getString(R.string.reset_email_task_successful)
                    )

                is FirebaseResult.Error -> {
                    resetPasswordMessage.value = when (FirebaseResult.errorType) {
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

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val dataSource: FirebaseAuthRepository,
        private val resourceProvider: ResourcesProvider
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java))
                return LoginViewModel(dataSource, resourceProvider) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}