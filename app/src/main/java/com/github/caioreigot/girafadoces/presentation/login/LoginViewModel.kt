package com.github.caioreigot.girafadoces.presentation.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.repository.FirebaseAuthRepository
import java.lang.IllegalArgumentException

class LoginViewModel(val dataSource: FirebaseAuthRepository) : ViewModel() {

    val loggedInLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent<Boolean>()
    val viewFlipperLiveData: MutableLiveData<Int> = MutableLiveData()
    val errorMessageLiveData: SingleLiveEvent<String> = SingleLiveEvent<String>()

    companion object {
        private const val VIEW_FLIPPER_LOGIN_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    fun loginUser(email: String, password: String) {
        // Show Progress Bar
        viewFlipperLiveData.value = VIEW_FLIPPER_PROGRESS_BAR

        dataSource.loginUser(email, password) { FirebaseResult ->

            viewFlipperLiveData.value = VIEW_FLIPPER_LOGIN_BUTTON

            when (FirebaseResult) {
                is FirebaseResult.Success -> loggedInLiveData.value = true
                is FirebaseResult.Error -> errorMessageLiveData.value = FirebaseResult.message
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(val firebaseAuthDataSource: FirebaseAuthRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass : Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java))
                return LoginViewModel(firebaseAuthDataSource) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}