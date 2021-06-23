package com.github.caioreigot.girafadoces.ui.main.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
class AddViewModel @Inject constructor(
    private val resProvider: ResourcesProvider,
    private val storage: StorageRepository,
    private val database: DatabaseRepository
) : ViewModel() {

    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val successMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()

    val menuItemsLD: MutableLiveData<MutableList<MenuItem>> = MutableLiveData()
    val uploadProgressLD: MutableLiveData<String> = MutableLiveData()

    fun getMenuItems() {
        database.getMenuItems(storage) { menuItems, result ->
            when (result) {
                is ServiceResult.Success -> menuItemsLD.value = menuItems

                is ServiceResult.Error -> {
                    errorMessageLD.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
                }
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
                        uploadProgressLD.value = "${percentageProgress}%"
                    },

                    // Callback for result
                    { _result ->
                        when (_result) {
                            is ServiceResult.Success -> {
                                successMessageLD.value =
                                    resProvider.getString(R.string.menu_item_save_successful)

                                callback(true)
                            }

                            is ServiceResult.Error -> {
                                errorMessageLD.value =
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
                    errorMessageLD.value = when (result.errorType) {
                        ErrorType.SERVER_ERROR ->
                            resProvider.getString(R.string.server_error_message)
                        else ->
                            resProvider.getString(R.string.unexpected_error_message)
                    }
                }
            }
        }
    }
}