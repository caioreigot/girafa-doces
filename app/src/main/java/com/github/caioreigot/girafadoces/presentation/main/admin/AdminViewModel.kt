package com.github.caioreigot.girafadoces.presentation.main.admin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.data.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.FirebaseResult
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.model.User
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import java.lang.IllegalArgumentException

class AdminViewModel(
    private val resProvider: ResourcesProvider,
    private val databaseSource: DatabaseRepository
) : ViewModel() {

    val adminUsersItemsLD: MutableLiveData<MutableList<User>> = MutableLiveData()
    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()

    val adminRemovedLD: MutableLiveData<Int> = MutableLiveData()
    val adminAddedLD: MutableLiveData<User> = MutableLiveData()

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

    fun addAdmin(email: String) {
        databaseSource.getAdministratorUidByEmail(email) { uid, result ->
            when (result) {
                is FirebaseResult.Success -> uid?.let {
                    addAdminToDatabase(uid) { addAdminResult ->
                        when (addAdminResult) {

                            // User UID caught successfully
                            is FirebaseResult.Success -> {

                                // Get User Data Class from database using UID previously caught
                                databaseSource.getUserByUid(uid) { user, getUserResult ->
                                    when (getUserResult) {
                                        is FirebaseResult.Success -> adminAddedLD.value = user

                                        is FirebaseResult.Error ->
                                            errorMessageLD.value = ErrorMessageHandler
                                                .getErrorMessage(resProvider, getUserResult.errorType)
                                    }
                                }
                            }

                            is FirebaseResult.Error ->
                                errorMessageLD.value = ErrorMessageHandler
                                    .getErrorMessage(resProvider, addAdminResult.errorType)
                        }
                    }
                }

                is FirebaseResult.Error ->
                    errorMessageLD.value = ErrorMessageHandler
                        .getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    private fun addAdminToDatabase(uid: String, callback: (FirebaseResult) -> Unit) =
        Singleton.mDatabaseAdminsReference.child(uid).setValue(true)
            .addOnSuccessListener { callback(FirebaseResult.Success) }
            .addOnFailureListener { callback(FirebaseResult.Error(ErrorType.SERVER_ERROR)) }

    fun removeAdmin(email: String, position: Int) {
        databaseSource.getAdministratorUidByEmail(email) { uid, result ->
            when (result) {
                is FirebaseResult.Success -> uid?.let {
                    removeAdminOfDatabase(uid) { result ->
                        when (result) {
                            is FirebaseResult.Success -> adminRemovedLD.value = position
                            is FirebaseResult.Error -> {/*TODO*/}
                        }
                    }
                }

                is FirebaseResult.Error ->
                    errorMessageLD.value = ErrorMessageHandler
                        .getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    private fun removeAdminOfDatabase(uid: String, callback: (FirebaseResult) -> Unit) =
        Singleton.mDatabaseAdminsReference.child(uid).removeValue()
            .addOnSuccessListener { callback(FirebaseResult.Success) }
            .addOnFailureListener { callback(FirebaseResult.Error(ErrorType.SERVER_ERROR)) }

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