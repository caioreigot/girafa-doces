package com.github.caioreigot.girafadoces.ui.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ErrorMessageHandler
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.helper.SingleLiveEvent
import com.github.caioreigot.girafadoces.data.model.User
import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.model.UserAccountField
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val resProvider: ResourcesProvider,
    private val database: DatabaseRepository
) : ViewModel() {

    private val _loadingViewFlipper: MutableLiveData<Int> = MutableLiveData()
    val loadingViewFlipper: LiveData<Int>
        get() = _loadingViewFlipper

    private val _reloadInformation: SingleLiveEvent<Unit> = SingleLiveEvent()
    val reloadInformation: LiveData<Unit>
        get() = _reloadInformation

    private val _successMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val successMessage: LiveData<String>
        get() = _successMessage

    private val _errorMessage: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _userAccountInformation: SingleLiveEvent<User> = SingleLiveEvent<User>()
    val userAccountInformation: LiveData<User>
        get() = _userAccountInformation

    companion object {
        private const val VIEW_FLIPPER_ACCOUNT_IMAGE = 1
    }

    fun changeAccountField(accountField: UserAccountField, newValue: Any) {
        database.changeAccountField(accountField, newValue) { result ->
            when (result) {
                is ServiceResult.Success ->
                    _successMessage.value = resProvider.getString(R.string.change_made_successfully)

                is ServiceResult.Error ->
                    _errorMessage.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    fun fetchUserAccountInformation() {
        database.getLoggedUserInformation { user, result ->
            when (result) {
                is ServiceResult.Success -> {
                    _userAccountInformation.value = user
                    _loadingViewFlipper.value = VIEW_FLIPPER_ACCOUNT_IMAGE
                }

                is ServiceResult.Error ->
                    _errorMessage.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    fun reloadInformation() = _reloadInformation.call()

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val resProvider: ResourcesProvider,
        private val database: DatabaseRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AccountViewModel::class.java))
                return AccountViewModel(resProvider, database) as T

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}