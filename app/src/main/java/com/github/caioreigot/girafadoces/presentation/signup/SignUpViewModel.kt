package com.github.caioreigot.girafadoces.presentation.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.repository.FirebaseAuthRepository
import java.lang.IllegalArgumentException

class SignUpViewModel(
    private val dataSource: FirebaseAuthRepository,
    private val resProvider: ResourcesProvider
) : ViewModel() {

    val registrationMade: SingleLiveEvent<Boolean> = SingleLiveEvent<Boolean>()

    val registerBtnViewFlipper: MutableLiveData<Int> = MutableLiveData()

    val errorMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val sucessfulMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()

    companion object {
        private const val VIEW_FLIPPER_REGISTER_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    fun registerUser(
        fullName: String,
        email: String,
        deliveryAddress: String,
        postalNumber: String,
        password: String,
        passwordConfirm: String
    ) {
        // Show Progress Bar
        registerBtnViewFlipper.value = VIEW_FLIPPER_PROGRESS_BAR

        dataSource.registerUser(
            fullName = fullName,
            email = email,
            deliveryAddress = deliveryAddress,
            postalNumber = postalNumber,
            password = password,
            passwordConfirm = passwordConfirm
        ) { FirebaseResult ->

            registerBtnViewFlipper.value = VIEW_FLIPPER_REGISTER_BUTTON

            when (FirebaseResult) {
                is FirebaseResult.Success -> {
                    sucessfulMessage.value = resProvider
                        .getString(R.string.signup_success_message)

                    registrationMade.value = true
                }

                is FirebaseResult.Error -> {
                    errorMessage.value = when (FirebaseResult.errorType) {
                        ErrorType.UNEXPECTED_ERROR ->
                            resProvider.getString(R.string.unexpected_error)

                        // Information Validation
                        ErrorType.EMPTY_FIELD ->
                            resProvider.getString(R.string.empty_field)

                        ErrorType.INVALID_NAME ->
                            resProvider.getString(R.string.invalid_name_message)

                        ErrorType.EMAIL_ALREADY_REGISTERED ->
                            resProvider.getString(R.string.email_already_registered)

                        ErrorType.INVALID_EMAIL ->
                            resProvider.getString(R.string.invalid_email_message)

                        ErrorType.WEAK_PASSWORD ->
                            resProvider.getString(R.string.weak_password_message)

                        ErrorType.PASSWORD_CONFIRM_DONT_MATCH ->
                            resProvider.getString(R.string.password_confirm_dont_match_message)

                        else ->
                            resProvider.getString(R.string.unexpected_error)
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
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java))
                return SignUpViewModel(dataSource, resourceProvider) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}