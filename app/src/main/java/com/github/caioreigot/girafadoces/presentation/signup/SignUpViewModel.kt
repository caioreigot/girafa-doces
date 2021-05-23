package com.github.caioreigot.girafadoces.presentation.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.repository.FirebaseAuthRepository
import java.lang.IllegalArgumentException

class SignUpViewModel(val dataSource: FirebaseAuthRepository) : ViewModel() {

    val registrationMadeLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent<Boolean>()
    val viewFlipperLiveData: MutableLiveData<Int> = MutableLiveData()
    val errorMessageLiveData: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val sucessfullMessageLiveData: SingleLiveEvent<String> = SingleLiveEvent<String>()

    companion object {
        private const val VIEW_FLIPPER_REGISTER_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        passwordConfirm: String) {

        // Show Progress Bar
        viewFlipperLiveData.value = VIEW_FLIPPER_PROGRESS_BAR

        dataSource.registerUser(
            fullName = fullName,
            email = email,
            password = password,
            passwordConfirm = passwordConfirm
        ) { FirebaseResult ->

            viewFlipperLiveData.value = VIEW_FLIPPER_REGISTER_BUTTON

            when (FirebaseResult) {
                is FirebaseResult.Success -> {
                    sucessfullMessageLiveData.value = FirebaseResult.message
                    registrationMadeLiveData.value = true
                }

                is FirebaseResult.Error -> {
                    errorMessageLiveData.value = FirebaseResult.message
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(val dataSource: FirebaseAuthRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass : Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java))
                return SignUpViewModel(dataSource) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}