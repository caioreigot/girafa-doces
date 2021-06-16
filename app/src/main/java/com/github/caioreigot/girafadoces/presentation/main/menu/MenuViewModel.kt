package com.github.caioreigot.girafadoces.presentation.main.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.model.FirebaseResult
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.MenuItem
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import com.github.caioreigot.girafadoces.data.repository.StorageRepository
import java.lang.IllegalArgumentException

class MenuViewModel(
    private val resProvider: ResourcesProvider,
    private val dataSource: DatabaseRepository,
    private val storageSource: StorageRepository
) : ViewModel() {

    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val menuItemsLD: MutableLiveData<MutableList<MenuItem>> = MutableLiveData()

    fun getMenuItems() {
        dataSource.getMenuItems(storageSource) { items, result ->
            when (result) {
                is FirebaseResult.Success -> {
                    if (items == null)
                        errorMessageLD.value = resProvider.getString(R.string.unexpected_error_message)

                    menuItemsLD.value = items
                }

                is FirebaseResult.Error -> {
                    errorMessageLD.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val resProvider: ResourcesProvider,
        private val dataSource: DatabaseRepository,
        private val storageSource: StorageRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MenuViewModel::class.java))
                return MenuViewModel(
                    resProvider,
                    dataSource,
                    storageSource
                ) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}