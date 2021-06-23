package com.github.caioreigot.girafadoces.ui.main.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.helper.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.MenuItem
import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import com.github.caioreigot.girafadoces.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val resProvider: ResourcesProvider,
    private val database: DatabaseRepository,
    private val storage: StorageRepository
) : ViewModel() {

    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val menuItemsLD: MutableLiveData<MutableList<MenuItem>> = MutableLiveData()

    fun getMenuItems() {
        database.getMenuItems(storage) { items, result ->
            when (result) {
                is ServiceResult.Success -> {
                    if (items == null)
                        errorMessageLD.value = resProvider.getString(R.string.unexpected_error_message)

                    menuItemsLD.value = items
                }

                is ServiceResult.Error -> {
                    errorMessageLD.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
                }
            }
        }
    }

    /*
    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val resProvider: ResourcesProvider,
        private val database: DatabaseRepository,
        private val storage: StorageRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MenuViewModel::class.java))
                return MenuViewModel(resProvider, database, storage) as T

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    */
}