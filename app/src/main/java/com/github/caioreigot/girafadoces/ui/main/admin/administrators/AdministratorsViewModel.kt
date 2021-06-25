package com.github.caioreigot.girafadoces.ui.main.admin.administrators

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.data.helper.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.helper.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.model.User
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdministratorsViewModel @Inject constructor(
    private val resProvider: ResourcesProvider,
    private val database: DatabaseRepository
) : ViewModel() {

    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val addAdminErrorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()

    val adminUsersItemsLD: MutableLiveData<MutableList<User>> = MutableLiveData()

    val adminAddedLD: MutableLiveData<User> = MutableLiveData()
    val adminRemovedLD: MutableLiveData<Int> = MutableLiveData()

    fun getAdministratorsUsers() {
        database.getAdministratorsUsers { adminUsers, result ->
            when (result) {
                is ServiceResult.Success -> adminUsersItemsLD.value = adminUsers

                is ServiceResult.Error ->
                    errorMessageLD.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    fun addAdmin(email: String) {
        database.getAdministratorUidByEmail(email) { uid, result ->
            when (result) {
                is ServiceResult.Success -> uid?.let { itUid ->
                    addAdminToDatabase(itUid) { addAdminResult ->
                        when (addAdminResult) {

                            // User UID caught successfully
                            is ServiceResult.Success -> {

                                // Get User Data Class from database using UID previously caught
                                database.getUserByUid(itUid) { user, getUserResult ->
                                    when (getUserResult) {
                                        is ServiceResult.Success -> adminAddedLD.value = user

                                        is ServiceResult.Error ->
                                            addAdminErrorMessageLD.value = ErrorMessageHandler
                                                .getErrorMessage(
                                                    resProvider,
                                                    getUserResult.errorType
                                                )
                                    }
                                }
                            }

                            is ServiceResult.Error ->
                                addAdminErrorMessageLD.value = ErrorMessageHandler
                                    .getErrorMessage(resProvider, addAdminResult.errorType)
                        }
                    }
                }

                is ServiceResult.Error ->
                    addAdminErrorMessageLD.value = ErrorMessageHandler
                        .getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    private fun addAdminToDatabase(uid: String, callback: (ServiceResult) -> Unit) =
        Singleton.mDatabaseAdminsReference.child(uid).setValue(true)
            .addOnSuccessListener { callback(ServiceResult.Success) }
            .addOnFailureListener { callback(ServiceResult.Error(ErrorType.SERVER_ERROR)) }

    fun removeAdmin(email: String, position: Int) {
        database.getAdministratorUidByEmail(email) { uid, result ->
            when (result) {
                is ServiceResult.Success -> uid?.let { itUid ->
                    removeAdminOfDatabase(itUid) { result ->
                        when (result) {
                            is ServiceResult.Success -> adminRemovedLD.value = position
                            is ServiceResult.Error -> {/*TODO*/}
                        }
                    }
                }

                is ServiceResult.Error ->
                    errorMessageLD.value = ErrorMessageHandler
                        .getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    private fun removeAdminOfDatabase(uid: String, callback: (ServiceResult) -> Unit) =
        Singleton.mDatabaseAdminsReference.child(uid).removeValue()
            .addOnSuccessListener { callback(ServiceResult.Success) }
            .addOnFailureListener { callback(ServiceResult.Error(ErrorType.SERVER_ERROR)) }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val resProvider: ResourcesProvider,
        private val database: DatabaseRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdministratorsViewModel::class.java))
                return AdministratorsViewModel(resProvider, database) as T

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}