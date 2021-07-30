package com.github.caioreigot.girafadoces.ui.main.menu.admin_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.helper.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.MenuItem
import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import com.github.caioreigot.girafadoces.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminMenuViewModel @Inject constructor(
    private val resProvider: ResourcesProvider,
    private val storage: StorageRepository,
    private val database: DatabaseRepository
) : ViewModel() {

    private val _errorMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _successMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val successMessage: LiveData<String>
        get() = _successMessage

    private val _menuItems: MutableLiveData<MutableList<MenuItem>> = MutableLiveData()
    val menuItems: LiveData<MutableList<MenuItem>>
        get() = _menuItems

    private val _uploadProgress: MutableLiveData<Int> = MutableLiveData()
    val uploadProgress: LiveData<Int>
        get() = _uploadProgress

    fun getMenuItems() {
        database.getMenuItems(storage) { menuItems, result ->
            when (result) {
                is ServiceResult.Success -> _menuItems.value = menuItems

                is ServiceResult.Error ->
                    _errorMessage.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    fun saveMenuItemDatabase(
        itemHeader: String,
        itemContent: String,
        itemImage: ByteArray,
        callback: (closeDialog: Boolean) -> Unit
    ) {
        database.saveMenuItem(itemHeader, itemContent) { uid, result ->
            uid?.let { imageUid ->
                storage.uploadImage(itemImage, imageUid,

                    // Callback for track progress of image upload
                    { percentageProgress ->
                        _uploadProgress.value = percentageProgress
                    },

                    // Callback for result
                    { _result ->
                        when (_result) {
                            is ServiceResult.Success -> {
                                _successMessage.value =
                                    resProvider.getString(R.string.menu_item_save_successful)

                                callback(true)
                            }

                            is ServiceResult.Error -> {
                                _errorMessage.value =
                                    ErrorMessageHandler.getErrorMessage(
                                        resProvider,
                                        _result.errorType
                                    )

                                callback(false)
                            }
                        }
                    })
            }

            when (result) {
                is ServiceResult.Success -> return@saveMenuItem

                is ServiceResult.Error -> {
                    _errorMessage.value = when (result.errorType) {
                        ErrorType.SERVER_ERROR ->
                            resProvider.getString(R.string.server_error_message)
                        else ->
                            resProvider.getString(R.string.unexpected_error_message)
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val resProvider: ResourcesProvider,
        private val storage: StorageRepository,
        private val database: DatabaseRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdminMenuViewModel::class.java))
                return AdminMenuViewModel(resProvider, storage, database) as T

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}