package com.github.caioreigot.girafadoces.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.caioreigot.girafadoces.data.helper.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.helper.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val resProvider: ResourcesProvider
) : ViewModel() {

    private val _registrationMade: SingleLiveEvent<Unit> = SingleLiveEvent<Unit>()
    val registrationMade: LiveData<Unit>
        get() = _registrationMade

    private val _registerBtnViewFlipper: MutableLiveData<Int> = MutableLiveData()
    val registerBtnViewFlipper: LiveData<Int>
        get() = _registerBtnViewFlipper

    val errorMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()

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
        _registerBtnViewFlipper.value = VIEW_FLIPPER_PROGRESS_BAR

        auth.registerUser(
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            deliveryAddress = deliveryAddress,
            postalNumber = postalNumber,
            password = password,
            passwordConfirm = passwordConfirm
        ) { result ->

            _registerBtnViewFlipper.value = VIEW_FLIPPER_REGISTER_BUTTON

            when (result) {
                is ServiceResult.Success -> _registrationMade.call()

                is ServiceResult.Error ->
                    errorMessage.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }
}