package com.github.caioreigot.girafadoces.presentation.main.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.FirebaseResult
import com.github.caioreigot.girafadoces.data.model.User
import com.github.caioreigot.girafadoces.data.model.UserAccountField
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import java.lang.IllegalArgumentException

class AccountViewModel(
    private val resProvider: ResourcesProvider,
    private val databaseSource: DatabaseRepository
) : ViewModel() {

    val loadingViewFlipperLD: MutableLiveData<Int> = MutableLiveData()
    val reloadInformationLD: SingleLiveEvent<Unit> = SingleLiveEvent()

    val successMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()

    val userAccountInformationLD: SingleLiveEvent<User> = SingleLiveEvent<User>()

    companion object {
        private const val VIEW_FLIPPER_PROGRESS_BAR = 0
        private const val VIEW_FLIPPER_ACCOUNT_IMAGE = 1
    }

    fun changeAccountField(accountField: UserAccountField, newValue: Any) {
        databaseSource.changeAccountField(accountField, newValue) { result ->
            when (result) {
                is FirebaseResult.Success ->
                    successMessageLD.value = resProvider.getString(R.string.change_made_successfully)

                is FirebaseResult.Error -> {
                    when (result.errorType) {
                        ErrorType.EMPTY_FIELD ->
                            errorMessageLD.value = resProvider.getString(R.string.empty_field)

                        ErrorType.INVALID_PHONE ->
                            errorMessageLD.value = resProvider.getString(R.string.invalid_phone_message)

                        else ->
                            errorMessageLD.value = resProvider.getString(R.string.unexpected_error)
                    }
                }
            }
        }
    }

    fun fetchUserAccountInformation() {
        databaseSource.getLoggedUserInformation { user, result ->
            when (result) {
                is FirebaseResult.Success -> {
                    userAccountInformationLD.value = user
                    loadingViewFlipperLD.value = VIEW_FLIPPER_ACCOUNT_IMAGE
                }

                is FirebaseResult.Error -> {
                    when (result.errorType) {
                        ErrorType.SERVER_ERROR -> errorMessageLD.value =
                            resProvider.getString(R.string.server_error_message)

                        else ->
                            resProvider.getString(R.string.unexpected_error)
                    }
                }
            }
        }
    }

    fun reloadInformation() = reloadInformationLD.call()

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val resProvider: ResourcesProvider,
        private val databaseSource: DatabaseRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AccountViewModel::class.java))
                return AccountViewModel(
                    resProvider,
                    databaseSource
                ) as T

            throw IllegalArgumentException("Unkown ViewModel class")
        }
    }
}