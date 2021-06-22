package com.github.caioreigot.girafadoces.ui.main.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    val loadingViewFlipperLD: MutableLiveData<Int> = MutableLiveData()
    val reloadInformationLD: SingleLiveEvent<Unit> = SingleLiveEvent()

    val successMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()
    val errorMessageLD: SingleLiveEvent<String> = SingleLiveEvent<String>()

    val userAccountInformationLD: SingleLiveEvent<User> = SingleLiveEvent<User>()

    companion object {
        /*private const val VIEW_FLIPPER_PROGRESS_BAR = 0*/
        private const val VIEW_FLIPPER_ACCOUNT_IMAGE = 1
    }

    fun changeAccountField(accountField: UserAccountField, newValue: Any) {
        database.changeAccountField(accountField, newValue) { result ->
            when (result) {
                is ServiceResult.Success ->
                    successMessageLD.value = resProvider.getString(R.string.change_made_successfully)

                is ServiceResult.Error ->
                    errorMessageLD.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    fun fetchUserAccountInformation() {
        database.getLoggedUserInformation { user, result ->
            when (result) {
                is ServiceResult.Success -> {
                    userAccountInformationLD.value = user
                    loadingViewFlipperLD.value = VIEW_FLIPPER_ACCOUNT_IMAGE
                }

                is ServiceResult.Error ->
                    errorMessageLD.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
            }
        }
    }

    fun reloadInformation() = reloadInformationLD.call()
}