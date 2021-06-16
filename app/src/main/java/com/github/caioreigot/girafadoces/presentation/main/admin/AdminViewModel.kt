package com.github.caioreigot.girafadoces.presentation.main.admin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.FirebaseResult
import com.github.caioreigot.girafadoces.data.model.User
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import java.lang.IllegalArgumentException

class AdminViewModel(
    private val resProvider: ResourcesProvider,
    private val databaseSource: DatabaseRepository
) : ViewModel() {

    val adminUsersItemsLD: MutableLiveData<MutableList<User>> = MutableLiveData()
    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()

    fun getAdministratorsUsers() {
        databaseSource.getAdministratorsUsers { adminUsers, result ->
            when (result) {
                is FirebaseResult.Success -> adminUsersItemsLD.value = adminUsers

                is FirebaseResult.Error ->
                    errorMessageLD.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val resProvider: ResourcesProvider,
        private val databaseSource: DatabaseRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdminViewModel::class.java))
                return AdminViewModel(resProvider, databaseSource) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}