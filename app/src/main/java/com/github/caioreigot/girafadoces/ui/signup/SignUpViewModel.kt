package com.github.caioreigot.girafadoces.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.data.helper.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.helper.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.repository.AuthRepository
import java.lang.IllegalArgumentException

class SignUpViewModel(
    private val auth: AuthRepository,
    private val resProvider: ResourcesProvider
) : ViewModel() {

    val registrationMadeLD: SingleLiveEvent<Boolean> = SingleLiveEvent<Boolean>()

    val registerBtnViewFlipperLD: MutableLiveData<Int> = MutableLiveData()

    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()

    companion object {
        private const val VIEW_FLIPPER_REGISTER_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    fun registerUser(
        fullName: String,
        email: String,
        phoneNumber: String,
        deliveryAddress: String,
        postalNumber: String,
        password: String,
        passwordConfirm: String,
    ) {
        // Show Progress Bar
        registerBtnViewFlipperLD.value = VIEW_FLIPPER_PROGRESS_BAR

        auth.registerUser(
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            deliveryAddress = deliveryAddress,
            postalNumber = postalNumber,
            password = password,
            passwordConfirm = passwordConfirm
        ) { result ->

            registerBtnViewFlipperLD.value = VIEW_FLIPPER_REGISTER_BUTTON

            when (result) {
                is ServiceResult.Success -> registrationMadeLD.value = true

                is ServiceResult.Error -> {
                    errorMessageLD.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val auth: AuthRepository,
        private val resourceProvider: ResourcesProvider
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java))
                return SignUpViewModel(auth, resourceProvider) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}