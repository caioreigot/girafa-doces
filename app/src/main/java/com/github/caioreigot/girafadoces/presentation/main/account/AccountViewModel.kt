package com.github.caioreigot.girafadoces.presentation.main.account

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.data.local.Preferences
import java.lang.IllegalArgumentException

class AccountViewModel(
    preferences: Preferences
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val preferences: Preferences
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AccountViewModel::class.java))
                return AccountViewModel(
                    preferences
                ) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}