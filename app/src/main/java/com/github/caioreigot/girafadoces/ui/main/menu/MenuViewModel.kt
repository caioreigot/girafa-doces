package com.github.caioreigot.girafadoces.ui.main.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.helper.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.MenuItem
import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import com.github.caioreigot.girafadoces.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val resProvider: ResourcesProvider,
    private val database: DatabaseRepository,
    private val storage: StorageRepository
) : ViewModel() {

    private val _errorMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _menuItems: MutableLiveData<MutableList<MenuItem>> = MutableLiveData()
    val menuItems: LiveData<MutableList<MenuItem>>
        get() = _menuItems

    fun getMenuItems() {
        database.getMenuItems(storage) { items, result ->
            when (result) {
                is ServiceResult.Success -> {
                    if (items == null)
                        _errorMessage.value =
                            resProvider.getString(R.string.unexpected_error_message)

                    _menuItems.value = items
                }

                is ServiceResult.Error ->
                    _errorMessage.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
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