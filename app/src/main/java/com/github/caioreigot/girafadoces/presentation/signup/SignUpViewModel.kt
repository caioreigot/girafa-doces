package com.github.caioreigot.girafadoces.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.data.remote.register.FirebaseRegisterRepository
import java.lang.IllegalArgumentException

class SignUpViewModel(val firebaseRegisterDataSource: FirebaseRegisterRepository) : ViewModel() {

    fun registerUser() {

    }
}

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(val firebaseRegisterDataSource: FirebaseRegisterRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass : Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java))
            return SignUpViewModel(firebaseRegisterDataSource) as T

        throw IllegalArgumentException("Unkown ViewModel class")
    }
}