package com.github.caioreigot.girafadoces.presentation.main.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.MenuItem
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import com.github.caioreigot.girafadoces.data.repository.StorageRepository
import java.lang.IllegalArgumentException

class AddViewModel(
    private val resProvider: ResourcesProvider,
    private val storageSource: StorageRepository,
    private val databaseSource: DatabaseRepository
) : ViewModel() {

    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val menuItemsLD: MutableLiveData<MutableList<MenuItem>> = MutableLiveData()
    val uploadProgressLD: MutableLiveData<String> = MutableLiveData()

    fun getMenuItems() {
        databaseSource.getMenuItems(storageSource) { menuItems, FirebaseResult ->
            when (FirebaseResult) {
                is FirebaseResult.Success -> menuItemsLD.value = menuItems

                is FirebaseResult.Error -> {
                    errorMessageLD.value = when (FirebaseResult.errorType) {
                        ErrorType.SERVER_ERROR ->
                            resProvider.getString(R.string.server_error_message)
                        else ->
                            resProvider.getString(R.string.unexpected_error)
                    }
                }
            }
        }
    }

    fun saveMenuItemDatabase(
        itemHeader: String,
        itemContent: String,
        itemImage: ByteArray
    ) {
        databaseSource.saveMenuItem(itemHeader, itemContent) { uid, FirebaseResult ->
            uid?.let {
                storageSource.uploadImage(itemImage, it,

                // Callback for track progress of image upload
                { percentageProgress ->
                    uploadProgressLD.value = "${percentageProgress}%"
                },

                // Callback for result
                { _FirebaseResult ->
                    when (_FirebaseResult) {
                        is FirebaseResult.Success -> {
                            // TODO
                        }

                        is FirebaseResult.Error -> {
                            // TODO
                        }
                    }
                })
            }

            when (FirebaseResult) {
                is FirebaseResult.Success -> {
                    // TODO
                }

                is FirebaseResult.Error -> {
                    errorMessageLD.value = when (FirebaseResult.errorType) {
                        ErrorType.SERVER_ERROR ->
                            resProvider.getString(R.string.server_error_message)
                        else ->
                            resProvider.getString(R.string.unexpected_error)
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val resProvider: ResourcesProvider,
        private val storageSource: StorageRepository,
        private val databaseSource: DatabaseRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddViewModel::class.java))
                return AddViewModel(
                    resProvider,
                    storageSource,
                    databaseSource
                ) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}