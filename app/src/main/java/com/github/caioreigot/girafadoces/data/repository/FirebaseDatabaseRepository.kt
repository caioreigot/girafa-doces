package com.github.caioreigot.girafadoces.data.repository

import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.model.User

interface FirebaseDatabaseRepository {
    fun getLoggedUserInformation(callback: (User?, FirebaseResult) -> Unit)
}