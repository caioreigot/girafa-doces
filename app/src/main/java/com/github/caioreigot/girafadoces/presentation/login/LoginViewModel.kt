package com.github.caioreigot.girafadoces.presentation.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.remote.auth.FirebaseAuthRepository
import java.lang.IllegalArgumentException

class LoginViewModel(val firebaseAuthDataSource: FirebaseAuthRepository) : ViewModel() {

    companion object {
        private const val VIEW_FLIPPER_LOGIN_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    val loginStatusLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val viewFlipperLiveData: MutableLiveData<Int> = MutableLiveData()
    val errorMessageLiveData: SingleLiveEvent<String> = SingleLiveEvent<String>()

    fun loginUser(email: String, password: String) {
        viewFlipperLiveData.value = VIEW_FLIPPER_PROGRESS_BAR

        firebaseAuthDataSource.loginUser(email, password) { loginStatus, errorMessage ->
            loginStatusLiveData.value = loginStatus
            viewFlipperLiveData.value = VIEW_FLIPPER_LOGIN_BUTTON

            errorMessage?.let {
                errorMessageLiveData.value = errorMessage
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